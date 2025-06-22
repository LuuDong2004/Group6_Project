package group6.cinema_project.controller;

import group6.cinema_project.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class to verify movie filtering functionality in the admin movie list
 * page.
 * This test ensures that the filtering interface correctly passes parameters to
 * the backend
 * and that the backend filtering methods are properly invoked.
 */
@WebMvcTest(MovieController.class)
public class MovieFilteringTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private MovieService movieService;

        /**
         * Test that the movie list page loads without filters
         */
        @Test
        public void testMovieListPageLoadsWithoutFilters() throws Exception {
                // Mock the service to return empty list
                when(movieService.getAllMoviesForDisplay()).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/admin/movies/list"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/admin_movie_list"))
                                .andExpect(model().attributeExists("movies"))
                                .andExpect(model().attribute("searchTerm", ""))
                                .andExpect(model().attribute("filterBy", "name"));

                // Verify that the service method was called
                verify(movieService).getAllMoviesForDisplay();
        }

        /**
         * Test filtering by movie name
         */
        @Test
        public void testFilterByMovieName() throws Exception {
                // Mock the service to return empty list for filtered search
                when(movieService.getFilteredMoviesForDisplay(eq("action"), eq("name")))
                                .thenReturn(Collections.emptyList());

                mockMvc.perform(get("/admin/movies/list")
                                .param("searchTerm", "action")
                                .param("filterBy", "name"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/admin_movie_list"))
                                .andExpect(model().attributeExists("movies"))
                                .andExpect(model().attribute("searchTerm", "action"))
                                .andExpect(model().attribute("filterBy", "name"));

                // Verify that the filtered service method was called with correct parameters
                verify(movieService).getFilteredMoviesForDisplay("action", "name");
        }

        /**
         * Test filtering by genre
         */
        @Test
        public void testFilterByGenre() throws Exception {
                when(movieService.getFilteredMoviesForDisplay(eq("comedy"), eq("genre")))
                                .thenReturn(Collections.emptyList());

                mockMvc.perform(get("/admin/movies/list")
                                .param("searchTerm", "comedy")
                                .param("filterBy", "genre"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/admin_movie_list"))
                                .andExpect(model().attribute("searchTerm", "comedy"))
                                .andExpect(model().attribute("filterBy", "genre"));

                verify(movieService).getFilteredMoviesForDisplay("comedy", "genre");
        }

        /**
         * Test filtering by director
         */
        @Test
        public void testFilterByDirector() throws Exception {
                when(movieService.getFilteredMoviesForDisplay(eq("Christopher Nolan"), eq("director")))
                                .thenReturn(Collections.emptyList());

                mockMvc.perform(get("/admin/movies/list")
                                .param("searchTerm", "Christopher Nolan")
                                .param("filterBy", "director"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/admin_movie_list"))
                                .andExpect(model().attribute("searchTerm", "Christopher Nolan"))
                                .andExpect(model().attribute("filterBy", "director"));

                verify(movieService).getFilteredMoviesForDisplay("Christopher Nolan", "director");
        }

        /**
         * Test filtering by actor
         */
        @Test
        public void testFilterByActor() throws Exception {
                when(movieService.getFilteredMoviesForDisplay(eq("Robert Downey Jr"), eq("actor")))
                                .thenReturn(Collections.emptyList());

                mockMvc.perform(get("/admin/movies/list")
                                .param("searchTerm", "Robert Downey Jr")
                                .param("filterBy", "actor"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/admin_movie_list"))
                                .andExpect(model().attribute("searchTerm", "Robert Downey Jr"))
                                .andExpect(model().attribute("filterBy", "actor"));

                verify(movieService).getFilteredMoviesForDisplay("Robert Downey Jr", "actor");
        }

        /**
         * Test filtering with empty search term (should load all movies)
         */
        @Test
        public void testFilterWithEmptySearchTerm() throws Exception {
                when(movieService.getAllMoviesForDisplay()).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/admin/movies/list")
                                .param("searchTerm", "")
                                .param("filterBy", "name"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/admin_movie_list"))
                                .andExpect(model().attribute("searchTerm", ""))
                                .andExpect(model().attribute("filterBy", "name"));

                // Should call getAllMoviesForDisplay when search term is empty
                verify(movieService).getAllMoviesForDisplay();
        }

        /**
         * Test default filterBy parameter when not specified
         */
        @Test
        public void testDefaultFilterByParameter() throws Exception {
                when(movieService.getFilteredMoviesForDisplay(eq("test"), eq("name")))
                                .thenReturn(Collections.emptyList());

                // Don't specify filterBy parameter - should default to "name"
                mockMvc.perform(get("/admin/movies/list")
                                .param("searchTerm", "test"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/admin_movie_list"))
                                .andExpect(model().attribute("searchTerm", "test"))
                                .andExpect(model().attribute("filterBy", "name"));

                verify(movieService).getFilteredMoviesForDisplay("test", "name");
        }
}
