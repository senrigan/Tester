package com.gdc.nms.tester.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.gdc.nms.model.NmsEntity;
import com.gdc.nms.model.NmsProperty;
import com.gdc.nms.model.Project;
import com.gdc.nms.server.EntityServiceLocal;

public class MyEntityService implements EntityServiceLocal {

    private static final MyEntityService INSTANCE = new MyEntityService();

    private List<Project> projects;

    private MyEntityService() {
        projects = new ArrayList<Project>();
    }

    @Override
    public <T extends NmsEntity> List<T> create(String paramString, List<T> paramList) {
        return null;
    }

    @Override
    public <T extends NmsEntity> T create(String paramString, T paramT) {
        return null;
    }

    @Override
    public <T extends NmsEntity> void createBulk(String paramString, Collection<T> paramCollection) {

    }

    @Override
    public void remove(String paramString, NmsEntity paramNmsEntity) {

    }

    @Override
    public void remove(String paramString, List<? extends NmsEntity> paramList) {

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getEntity(Class<T> clazz, long id, boolean paramBoolean) {
        if (clazz.equals(Project.class)) {
            for (Project project : projects) {
                if (project.getId() == id) {
                    return (T) project;
                }
            }
        }
        return null;
    }

    @Override
    public <T> T getEntity(String paramString, Map<?, ?> paramMap, boolean paramBoolean1, boolean paramBoolean2) {
        return null;
    }

    @Override
    public <T> List<T> getEntities(String paramString, Map<?, ?> paramMap, boolean paramBoolean1, boolean paramBoolean2) {
        return null;
    }

    @Override
    public <T> List<T> getEntities(String paramString, Map<?, ?> paramMap, boolean paramBoolean1,
            boolean paramBoolean2, int paramInt1, int paramInt2) {
        return null;
    }

    @Override
    public <T extends NmsEntity> void update(String paramString, Class<T> paramClass, long paramLong,
            NmsProperty paramNmsProperty, Object paramObject) {

    }

    @Override
    public <T extends NmsEntity> void update(String paramString, Class<T> paramClass, long paramLong,
            Map<NmsProperty, Object> paramMap) {

    }

    @Override
    public <T extends NmsEntity> void update(String paramString, Class<T> paramClass, Collection<Long> paramCollection,
            NmsProperty paramNmsProperty, Object paramObject) {

    }

    @Override
    public <T extends NmsEntity> void update(String paramString, Class<T> paramClass, Collection<Long> paramCollection,
            Map<NmsProperty, Object> paramMap) {

    }

    @Override
    public <T extends NmsEntity> void remove(String paramString, Class<T> paramClass, long paramLong) {

    }

    @Override
    public <T extends NmsEntity> void remove(String paramString, Class<T> paramClass, Collection<Long> paramCollection) {

    }

    @Override
    public int executeNativeUpdate(String paramString, Map<?, ?> paramMap) {
        return 0;
    }

    @Override
    public int executeNamedUpdate(String paramString, Map<?, ?> paramMap) {
        return 0;
    }

    @Override
    public <T> List<T> executeNativeQuery(String paramString, Map<?, ?> paramMap, Class<T> paramClass) {
        return null;
    }

    @Override
    public <T extends NmsEntity> T lupdate(String paramString, Class<T> paramClass, long paramLong,
            NmsProperty paramNmsProperty, Object paramObject) {
        return null;
    }

    @Override
    public <T extends NmsEntity> T lupdate(String paramString, Class<T> paramClass, long paramLong,
            Map<NmsProperty, Object> paramMap) {
        return null;
    }

    @Override
    public <T extends NmsEntity> T lupdate(String paramString, NmsEntity paramNmsEntity, NmsProperty paramNmsProperty,
            Object paramObject) {
        return null;
    }

    @Override
    public <T extends NmsEntity> T lupdate(String paramString, NmsEntity paramNmsEntity,
            Map<NmsProperty, Object> paramMap) {
        return null;
    }

    @Override
    public <T> List<T> getEntities(String paramString, Class<T> paramClass, boolean paramBoolean, int paramInt1,
            int paramInt2) {
        return null;
    }

    public static MyEntityService getInstance() {
        return INSTANCE;
    }

    public void setProjects(Collection<Project> projects) {
        if (projects == null) {
            return;
        }
        this.projects.addAll(projects);
    }
}
