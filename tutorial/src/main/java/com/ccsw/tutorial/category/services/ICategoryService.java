package com.ccsw.tutorial.category.services;

import com.ccsw.tutorial.category.dto.CategoryDto;
import com.ccsw.tutorial.category.model.Category;


import java.util.List;

/**
 * @author ccsw
 *
 */
public interface ICategoryService {

    /**
     * Método para recuperar todas las {@link Category}
     *
     * @return {@link List} de {@link Category}
     */
    List<Category> findAll();

    /**
     * Método para crear o actualizar una {@link Category}
     *
     * @param id PK de la entidad
     * @param dto datos de la entidad
     */
    void save(Long id, CategoryDto dto);

    /**
     * Método para borrar una {@link Category}
     *
     * @param id PK de la entidad
     */
    void delete(Long id) throws Exception;

}