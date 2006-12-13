package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.model.Language;


public interface LanguageDAO extends BaseDAO<Language> {

    public Language findByKeyAndLocale(String key, String base_locale);

    public List<Language> findByKey(String key);

    public List<Language> findByLocale(String base_locale);
    
}
