# DTO Package Documentation

## Overview
This package contains Data Transfer Objects (DTOs) used for transferring data between the controller layer and client applications. DTOs help separate the internal entity structure from the external API contract.

## DTO Classes

### MovieDetailDTO
- **Purpose**: Comprehensive movie information including related data
- **Fields**: id, name, image, duration, release_date, rating, genre, language, format, trailer, summary, directors, actors, reviews, actorsData, directorsData
- **Usage**: Used in movie-related endpoints to provide complete movie details

### UserDTO
- **Purpose**: User information for API responses
- **Fields**: id, username, email, fullName, phone, address
- **Usage**: Used in user-related endpoints to avoid exposing sensitive data

### ActorDTO
- **Purpose**: Actor information for API responses
- **Fields**: id, name, image, biography, birthDate, nationality
- **Usage**: Used in actor-related endpoints

### DirectorDTO
- **Purpose**: Director information for API responses
- **Fields**: id, name, image, biography, birthDate, nationality
- **Usage**: Used in director-related endpoints

### ReviewDTO
- **Purpose**: Review information for API responses
- **Fields**: user, comment, rating, date
- **Usage**: Used as part of MovieDetailDTO for movie reviews

### PersonDTO
- **Purpose**: Basic person information (used for actors and directors in movie details)
- **Fields**: id, name
- **Usage**: Used as part of MovieDetailDTO for simplified actor/director information

## Best Practices

1. **Separation of Concerns**: DTOs are kept separate from controllers and entities
2. **Single Responsibility**: Each DTO has a specific purpose and contains only relevant fields
3. **Data Protection**: Sensitive information is excluded from DTOs
4. **Consistency**: DTOs follow consistent naming conventions and structure
5. **Reusability**: DTOs can be reused across different endpoints

## Usage Example

```java
// In Controller
@GetMapping("/movies/{id}")
public ResponseEntity<MovieDetailDTO> getMovieDetail(@PathVariable Long id) {
    MovieDetailDTO dto = movieService.getMovieDetail(id);
    return ResponseEntity.ok(dto);
}

// In Service
public MovieDetailDTO getMovieDetail(Long id) {
    Movie movie = movieRepository.findById(id).orElse(null);
    if (movie == null) return null;
    
    MovieDetailDTO dto = new MovieDetailDTO();
    // Map entity to DTO
    return dto;
}
``` 