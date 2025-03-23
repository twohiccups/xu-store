package com.xu_store.uniform.controller


import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController @RequestMapping("/auth")
class AuthController() {

    @GetMapping("user")
    fun helloUser(): String {
        return "hello User"
    }


    @GetMapping("admin")
    fun helloAdmin(): String {
        return "Hello Admin"
    }

    // For demonstration purposes, this method uses static credentials.
//    // In production, use a service to authenticate against a user repository.
//    @PostMapping("/login")
//    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Any> {
//        // Replace this with real authentication logic
//        if (loginRequest.username == "user" && loginRequest.password == "password") {
//            val token = jwtTokenProvider.createToken(loginRequest.username, listOf("ROLE_USER"))
//            return ResponseEntity.ok(mapOf("token" to token))
//        }
//        return ResponseEntity.status(401).body("Invalid credentials")
//    }


}

