package idv.rennnhong.backendstarterkit.service.Impl;

import com.google.common.collect.ImmutableList;
import idv.rennnhong.backendstarterkit.controller.request.api.CreateApiRequestDto;
import idv.rennnhong.backendstarterkit.controller.request.api.UpdateApiRequestDto;
import idv.rennnhong.backendstarterkit.dto.ApiDto;
import idv.rennnhong.backendstarterkit.dto.RoleDto;
import idv.rennnhong.backendstarterkit.dto.mapper.ApiMapper;
import idv.rennnhong.backendstarterkit.model.dao.ApiDao;
import idv.rennnhong.backendstarterkit.model.dao.RoleDao;
import idv.rennnhong.backendstarterkit.model.entity.Api;
import idv.rennnhong.backendstarterkit.model.entity.Role;
import idv.rennnhong.backendstarterkit.model.entity.RolePermission;
import idv.rennnhong.backendstarterkit.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ApiServiceImpl implements ApiService {

    final ApiDao apiDao;

    final ApiMapper apiMapper;

    RoleDao roleDao;

    @Autowired
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Autowired
    public ApiServiceImpl(ApiDao apiDao, ApiMapper apiMapper) {
        this.apiDao = apiDao;
        this.apiMapper = apiMapper;
    }

    @Override
    public List<ApiDto> getAllApiByRole(UUID roleId, String url, String httpMethod) {
        Role roleEntity = roleDao.findById(roleId).get();
        Set<RolePermission> rolePermissions = roleEntity.getRolePermissions();

//        List<Api> apiList = rolePermissions.stream()
//            .map(rolePermission -> rolePermission.getAction().getApi())
//            .filter(item -> url.matches(item.getUrl() + "/.*"))
//            .collect(Collectors.toList());
        return ImmutableList.copyOf(apiMapper.toDto(getApiList(rolePermissions, url)));
    }

    @Override
    public Collection<ApiDto> getAll() {
        List<Api> apis = apiDao.findAll();
        return apiMapper.toDto(apis);
    }

    @Override
    public ApiDto getById(UUID id) {
        Api api = apiDao.findById(id).get();
        return apiMapper.toDto(api);
    }

    @Override
    public ApiDto save(CreateApiRequestDto createApiRequestDto) {
        Api entity = apiMapper.createEntity(createApiRequestDto);
        apiDao.save(entity);
        return apiMapper.toDto(entity);
    }

    @Override
    public ApiDto update(UUID id, UpdateApiRequestDto updateApiRequestDto) {
        Api api = apiDao.findById(id).get();
        apiMapper.updateEntity(api, updateApiRequestDto);
        return apiMapper.toDto(api);
    }

    @Override
    public void delete(UUID id) {
        apiDao.deleteById(id);
    }

    @Override
    public boolean isExist(UUID id) {
        return apiDao.existsById(id);
    }

    @Override
    public List<ApiDto> getAllApiByRoles(List<UUID> roleIds, String url, String httpMethod) {

        List<Role> roleEntities = roleDao.findAllByIdIn(roleIds);

        List<Set<RolePermission>> collect = roleEntities.stream().map(roleEntity -> roleEntity.getRolePermissions())
                .collect(Collectors.toList());

        List<Api> roleApiList = collect.stream()
                .map(rolePermissions -> getApiList(rolePermissions, url))
                .reduce((resultList, list) -> {
                    resultList.addAll(list);
                    return resultList;
                }).get();

//        List<List<ApiDTO>> roleApiList = roles
//            .stream()
//            .map(role -> getAllApiByRole(role, url, httpMethod))
//            .collect(Collectors.toList());

//        roleApiList
//            .stream()
//            .forEach(apis -> apiList.addAll(apis));
        return ImmutableList.copyOf(apiMapper.toDto(roleApiList));
    }

    private List<Api> getApiList(Set<RolePermission> rolePermissions, String url) {
        return rolePermissions.stream()
                .map(rolePermission -> rolePermission.getAction().getApi())
                .filter(item -> url.matches(item.getUrl() + "/.*"))
                .collect(Collectors.toList());
    }
}
