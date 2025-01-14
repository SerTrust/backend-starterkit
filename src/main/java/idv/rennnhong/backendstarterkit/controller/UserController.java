package idv.rennnhong.backendstarterkit.controller;

import static idv.rennnhong.common.response.ErrorMessages.*;

import idv.rennnhong.common.query.PageableResult;
import idv.rennnhong.common.response.ResponseBody;
import idv.rennnhong.backendstarterkit.controller.request.user.CreateUserRequestDto;
import idv.rennnhong.backendstarterkit.controller.request.user.UpdateUserRequestDto;
import idv.rennnhong.backendstarterkit.dto.UserDto;
import idv.rennnhong.backendstarterkit.dto.mapper.UserMapper;
import idv.rennnhong.backendstarterkit.service.PermissionService;
import idv.rennnhong.backendstarterkit.service.UserService;
import idv.rennnhong.backendstarterkit.web.utils.JwtTokenUtils;
import idv.rennnhong.backendstarterkit.web.utils.MapValidationErrorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

import java.util.Collection;
import java.util.UUID;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Api(tags = {"使用者資料"})
@SwaggerDefinition(tags = {
        @Tag(name = "使用者資料", description = "使用者資料API文件")
})
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private MapValidationErrorService mapValidationErrorService;
    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    @ApiOperation("取得使用者資料")
    public ResponseEntity getUsers(@RequestParam(defaultValue = "1") int pageNumber,
                                   @RequestParam(defaultValue = "10") int rowsPerPage) {
        PageableResult<UserDto> result = userService.pageAll(pageNumber, rowsPerPage);
        ResponseBody<Collection<UserDto>> responseBody = ResponseBody.newPageableBody(result);
        return new ResponseEntity(responseBody, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ApiOperation("查詢使用者資料 By userId")
    public ResponseEntity<Object> getUserByUserId(@PathVariable UUID id) {
        if ("".equals(id)) {
            return new ResponseEntity(ResponseBody.newErrorMessageBody(INVALID_FIELDS_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(userService.getById(id), HttpStatus.OK);
    }

    @PostMapping
    @ApiOperation("建立使用者資料")
    public ResponseEntity<Object> createUser(@RequestBody CreateUserRequestDto createUserRequestDto) {
        if (!userService.isExistByAccount(createUserRequestDto.getAccount())) {
            UserDto savedUserDto = userService.save(createUserRequestDto);
            return new ResponseEntity(ResponseBody.newSingleBody(savedUserDto), HttpStatus.OK);
        }
        return new ResponseEntity(
                ResponseBody.newErrorMessageBody(REQUEST_DUPLICATE_DATA),
                HttpStatus.BAD_REQUEST);
    }


    @GetMapping(params = {"account"})
    @ApiOperation("查詢使用者資料 By Account")
    public ResponseEntity<Object> getUserByUserAccount(@RequestParam("account") String account) {
        if ("".equals(account)) {
            return new ResponseEntity<Object>(ResponseBody.newErrorMessageBody(INVALID_FIELDS_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
        UserDto user = userService.getUserByAccount(account);
        ResponseBody<UserDto> responseBody = ResponseBody.newSingleBody(user);
        return new ResponseEntity(responseBody, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ApiOperation("更新使用者資料")
    public ResponseEntity<Object> updateUser(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateUserRequestDto updateUserRequestDto,
            BindingResult bindingResult) {
        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(bindingResult);
        if (errorMap != null) {
            return new ResponseEntity(ResponseBody.newErrorMessageBody(INVALID_FIELDS_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }

        UserDto userDto = userService.getById(id);
        if (userDto == null) {
            return new ResponseEntity(ResponseBody.newErrorMessageBody(RESOURCE_NOT_FOUND), HttpStatus.NOT_FOUND);
        }
        UserDto updatedUserDto = userService.update(id, updateUserRequestDto);
        return new ResponseEntity(updatedUserDto, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    @ApiOperation("刪除使用者資料")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        if ("".equals(id)) {
            return new ResponseEntity(ResponseBody.newErrorMessageBody(INVALID_FIELDS_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }

        if (!userService.isExist(UUID.fromString(id))) {
            return new ResponseEntity(ResponseBody.newErrorMessageBody(COULD_NOT_DELETE_RECORD),
                    HttpStatus.BAD_REQUEST);
        }

        userService.delete(UUID.fromString(id));
        return new ResponseEntity(HttpStatus.NO_CONTENT);

    }

}
