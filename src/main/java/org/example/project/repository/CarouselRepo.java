package org.example.project.repository;

import org.example.project.entity.Carousel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface CarouselRepo extends JpaRepository<Carousel,Integer> {
    Collection<Object> findByIsActiveTrueOrderByOrderIndexAsc();
}
