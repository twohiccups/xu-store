# Order Placement & Credit Flow

## High-Level Sequence
1. **Request entry (`OrderController.placeOrder`)** - Validates authentication, looks up the calling `User` by email, and pushes the `CreateOrderRequest` + user id to `OrderService.placeOrder` (`src/main/kotlin/com/xu_store/uniform/controller/OrderController.kt:22`).
2. **Service orchestration (`OrderService.placeOrder`)** - Retrieves the `User`/`Team`, calculates financial totals, enforces credit sufficiency, saves the `Order` aggregate, and logs the resulting credit transaction (`src/main/kotlin/com/xu_store/uniform/service/OrderService.kt:27`).
3. **Credit persistence (`UserService` & `CreditService`)** - `UserService.deductUserCreditsOrThrow` performs an atomic balance update at the database level, and `CreditService.logOrderTransaction` records the journal entry tied to the saved order (`src/main/kotlin/com/xu_store/uniform/service/UserService.kt:45`, `src/main/kotlin/com/xu_store/uniform/service/CreditService.kt:56`).
4. **Response mapping** - The saved `Order` is serialized via `OrderResponse.from` and wrapped in `CreateOrderResponse` for the HTTP payload (`src/main/kotlin/com/xu_store/uniform/dto/OrderResponse.kt:13`).

```kotlin
// Condensed happy-path from OrderService.placeOrder
val user = userService.getUserById(userId)
val totalAmount = request.orderItems.sumOf { it.quantity * findPrice(it.productVariationId) } + requireNotNull(user.team).shippingFee
userService.deductUserCreditsOrThrow(userId, totalAmount)
val savedOrder = orderRepository.save(buildOrder(user, totalAmount, request))
creditService.logOrderTransaction(userId, -totalAmount, "Order #${savedOrder.id}", savedOrder)
```

## Domain & Persistence Model
- `Order` / `OrderItem` / `Payment` live under `src/main/kotlin/com/xu_store/uniform/model`. `Order` captures address and monetary fields plus relations to `User`, `Team`, and child collections.
- `ProductVariation.price` is the authoritative unit price; the service fetches it for each line item (`ProductRepository.findVariationPrice`, `ProductRepository.findProductVariationById`).
- Credit transactions (`CreditTransaction`) represent the ledger for every balance change and link back to `Order` when applicable.

### Matching SQL Schema
- `orders` table stores user/team ids, shipping fees, aggregate totals, and addresses (`sql/schema.sql:33`).
- `order_items` captures the product/variation, quantity, and `unit_price` snapshot (`sql/schema.sql:51`).
- `users.store_credits` is decremented via an atomic `UPDATE ... WHERE store_credits >= :amount` (`sql/schema.sql:12` + `UserRepository.deductUserCredits`).
- `credit_transactions` holds the signed delta and optional `order_id` foreign key (`sql/schema.sql:77`).

## Credit Handling Details
- Balance checks never happen in memory. `UserService.deductUserCreditsOrThrow` issues a single SQL update; the row count indicates whether enough credits were available (`src/main/kotlin/com/xu_store/uniform/service/UserService.kt:49`).
- On success, `CreditService.logOrderTransaction` writes a negative amount transaction tied to the order (`src/main/kotlin/com/xu_store/uniform/service/CreditService.kt:59`).
- If the balance is insufficient, `deductUserCreditsOrThrow` throws `NotEnoughCreditsException`, preventing order persistence and transaction logging.

## Tests Guarding the Flow
- `OrderServiceTests` covers both the happy path and the insufficient credit path. The positive test captures the saved order to assert the computed `totalAmount` and verifies that credit deduction + transaction logging were invoked (`src/test/kotlin/com/xu_store/uniform/service/OrderServiceTest.kt:95`).
- The failure test stubs `deductUserCreditsOrThrow` to throw and confirms no order save or credit logging occurs (`src/test/kotlin/com/xu_store/uniform/service/OrderServiceTest.kt:172`).

## Observations & Gaps
- `OrderController.placeOrder` currently only catches `IllegalArgumentException`. When `UserService.deductUserCreditsOrThrow` throws `NotEnoughCreditsException`, the controller lets it propagate, which will return a `500` unless a higher-level handler maps it. Consider catching `NotEnoughCreditsException` (or adding global advice) to return `422 UNPROCESSABLE_ENTITY` with the message (`src/main/kotlin/com/xu_store/uniform/controller/OrderController.kt:25`). ***Implementation discrepancy worth addressing.***
- Schema requires `orders.team_id NOT NULL`, while `Order.team` is nullable in the entity. The service throws if `team` is missing, so the runtime path stays consistent, but double-check historical data before enforcing nullability constraints.

Overall, the order placement pipeline cleanly sequences request handling, price lookup, credit enforcement, persistence, and ledger logging, with targeted unit tests ensuring the credit coupling remains intact.