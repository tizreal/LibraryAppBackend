package com.tca.libraryApp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tca.libraryApp.entity.User;
import com.tca.libraryApp.service.UserService;

//@RestController = @Controller + @ResponseBody. 
// Every return value is automatically serialised to JSON and sent in the HTTP response body. 
// @RequestMapping sets the base URL for all methods in this class.
//@CrossOrigin allows a browser-based frontend (e.g. React on localhost:5173) 
//to call this API without being blocked by CORS security policy. 
//Without this annotation, the browser will refuse to show the API response. 
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST,
		RequestMethod.PUT, RequestMethod.DELETE })

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	// POST /api/users
	// @RequestBody tells Spring to deserialise the JSON request body into a User
	// object.
	// ResponseEntity lets us control the HTTP status code we send back.

	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user) {
		User createdUser = userService.createUser(user);
		return ResponseEntity.ok(createdUser); // 200 OK + JSON body
	}

	// GET /api/users
	@GetMapping
	public ResponseEntity<List<User>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	// GET /api/users/{userId}
	// @PathVariable extracts the {userId} segment from the URL path.
	@GetMapping("/{userId}")
	public ResponseEntity<User> getUserById(@PathVariable Integer userId) {
		Optional<User> user = userService.getUserById(userId);
		// map() wraps the found user in 200 OK.
		// orElseGet() returns 404 Not Found if the Optional is empty.
		return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	
	@PutMapping("/{userId}")
	public ResponseEntity<User> updateUser(@PathVariable Integer userId, @RequestBody User updatedUser){
		User user = userService.updateUser(userId, updatedUser);
		return ResponseEntity.ok(user);
	}

	// DELETE /api/users/{userId}
	// 204 No Content is the correct HTTP response for a successful delete with no
	// body.
	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
		userService.deleteUser(userId);
		return ResponseEntity.noContent().build(); // 204 No Content
	}

}
