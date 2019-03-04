package com.demo.controller;

import com.demo.common.ConstDatas;
import com.demo.dto.CommonResult;
import com.demo.exception.CommonError;
import com.demo.model.Account;
import com.demo.model.Contest;
import com.demo.model.Grade;
import com.demo.model.Subject;
import com.demo.service.*;
import com.demo.util.MD5;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/account")
public class AccountController {

    private static Log LOG = LogFactory.getLog(AccountController.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private PostService postService;
    @Autowired
    private GradeService gradeService;
    @Autowired
    private ContestService contestService;
    @Autowired
    private SubjectService subjectService;

    /**
     * 个人信息页面
     */
    @GetMapping(value="/profile")
    public String profile(HttpServletRequest request, Model model, Account currentAccount) {
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        return "user/profile";
    }

    /**
     * 更改密码页面
     */
    @GetMapping(value="/password")
    public String password(HttpServletRequest request, Model model, Account currentAccount) {
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        return "user/password";
    }

    /**
     * 考试记录页面
     */
    @GetMapping(value="/myExam")
    public String myExam(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int page, Model model, Account currentAccount) {
        Map<String, Object> data = gradeService.getGradesByStudentId(page, ConstDatas.gradePageSize, currentAccount.getId());
        List<Grade> grades = (List<Grade>) data.get("grades");
        Set<Integer> contestIds = grades.stream().map(Grade::getContestId).collect(Collectors.toCollection(HashSet::new));
        List<Contest> contests = contestService.getContestsByContestIds(contestIds);
        List<Subject> subjects = subjectService.getSubjects();
        Map<Integer, String> subjectId2name = subjects.stream().
                collect(Collectors.toMap(Subject::getId, Subject::getName));
        for (Contest contest : contests) {
            contest.setSubjectName(subjectId2name.
                    getOrDefault(contest.getSubjectId(), "未知科目"));
        }
        Map<Integer, Contest> id2contest = contests.stream().
                collect(Collectors.toMap(Contest::getId, contest -> contest));
        for (Grade grade : grades) {
            grade.setContest(id2contest.get(grade.getContestId()));
        }
        model.addAttribute(ConstDatas.DATA, data);
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        return "user/myExam";
    }

    /**
     * 我的发帖页面
     */
    @GetMapping(value="/myDiscussPost")
    public String myDiscussPost(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int page, Model model, Account currentAccount) {
        Map<String, Object> data = postService.getPostsByAuthorId(page, ConstDatas.postPageSize, currentAccount.getId());
        model.addAttribute(ConstDatas.DATA, data);
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        return "user/myDiscussPost";
    }

    /**
     * 更新密码
     */
    @PostMapping(value = "/api/updatePassword")
    @ResponseBody
    public CommonResult updatePassword(HttpServletRequest request, HttpServletResponse response,Account currentAccount) {
        try {
            String oldPassword = request.getParameter("oldPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmNewPassword = request.getParameter("confirmNewPassword");
            String md5OldPassword = MD5.md5(ConstDatas.MD5_SALT+oldPassword);
            String md5NewPassword = MD5.md5(ConstDatas.MD5_SALT+newPassword);
            if (StringUtils.isNotEmpty(newPassword) && StringUtils.isNotEmpty(confirmNewPassword)
                    && !newPassword.equals(confirmNewPassword)) {
                return CommonResult.fixedError(CommonError.NOT_EQUALS_CONFIRM_PASSWORD);
            }
            if (!currentAccount.getPassword().equals(md5OldPassword)) {
                return CommonResult.fixedError(CommonError.WRONG_PASSWORD);
            }
            currentAccount.setPassword(md5NewPassword);
            boolean result = accountService.updateAccount(currentAccount);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return CommonResult.fixedError(CommonError.COMMON);
        }
        return new CommonResult();
    }

    /**
     * 更新个人信息
     */
    @PostMapping(value = "/api/updateAccount")
    @ResponseBody
    public CommonResult updateAccount(HttpServletRequest request, HttpServletResponse response,Account currentAccount) {
        try {
            String phone = request.getParameter("phone");
            String qq = request.getParameter("qq");
            String email = request.getParameter("email");
            String description = request.getParameter("description");
            String avatarImgUrl = request.getParameter("avatarImgUrl");
            currentAccount.setPhone(phone);
            currentAccount.setQq(qq);
            currentAccount.setEmail(email);
            currentAccount.setDescription(description);
            currentAccount.setAvatarImgUrl(avatarImgUrl);
            boolean result = accountService.updateAccount(currentAccount);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return CommonResult.fixedError(CommonError.COMMON);
        }
        return new CommonResult();
    }

    /**
     * 验证登录
     */
    @PostMapping(value = "/api/login")
    @ResponseBody
    public CommonResult login(HttpServletRequest request, HttpServletResponse response) {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            Account current_account = accountService.getAccountByUsername(username);
            if(current_account != null) {
                String pwd = MD5.md5(ConstDatas.MD5_SALT+password);
                if(pwd.equals(current_account.getPassword())) {
                    //设置单位为秒，设置为-1永不过期
                    //request.getSession().setMaxInactiveInterval(180*60);    //3小时
                    request.getSession().setAttribute(ConstDatas.CURRENT_ACCOUNT,current_account);
                } else {
                    return CommonResult.fixedError(CommonError.WRONG_PASSWORD);
                }
            } else {
                return CommonResult.fixedError(CommonError.WRONG_USERNAME);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return CommonResult.fixedError(CommonError.COMMON);
        }
        return new CommonResult();
    }

    /**
     * 用户退出
     * @param request
     * @return
     */
    @GetMapping(value = "/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().setAttribute(ConstDatas.CURRENT_ACCOUNT,null);
        String url=request.getHeader("Referer");
        LOG.info("url = " + url);
        return "redirect:"+url;
    }

    /**
     * 上传头像
     */
    @PostMapping(value = "/api/uploadAvatar")
    @ResponseBody
    public Map<String,Object> uploadAvatar(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws IllegalStateException, IOException{
        try {
            //原始名称
            String oldFileName = file.getOriginalFilename(); //获取上传文件的原名
            //存储图片的物理路径
            String file_path = ConstDatas.UPLOAD_FILE_IMAGE_PATH;
            LOG.info("file_path = " + file_path);
            //上传图片
            if(file!=null && oldFileName!=null && oldFileName.length()>0){
                //新的图片名称
                String newFileName = UUID.randomUUID() + oldFileName.substring(oldFileName.lastIndexOf("."));
                //新图片
                File newFile = new File(file_path+newFileName);
                //将内存中的数据写入磁盘
                file.transferTo(newFile);
                //将新图片名称返回到前端
            }else{
                return CommonResult.fixedError(CommonError.UPLOAD_FILE_IMAGE_NOT_QUALIFIED);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return CommonResult.fixedError(CommonError.UPLOAD_FILE_IMAGE_ANALYZE_ERROR);
        }
        return new CommonResult();
    }

    /**
     * API:添加用户
     */
    @PostMapping(value="/api/addAccount")
    @ResponseBody
    public CommonResult addAccount(@RequestBody Account account){
        CommonResult commonResult = new CommonResult();
        Account existAccount = accountService.getAccountByUsername(account.getUsername());
        if(existAccount == null) {//检测该用户是否已经注册
            account.setPassword(MD5.md5(ConstDatas.MD5_SALT+account.getPassword()));
            account.setAvatarImgUrl(ConstDatas.DEFAULT_AVATAR_IMG_URL);
            account.setDescription("");
            int accountId = accountService.addAccount(account);
            return new CommonResult();
        }
        return CommonResult.fixedError(CommonError.AREADY_EXIST_USERNAME);
    }

    /**
     * API:更新用户
     */
    @PostMapping(value="/api/updateManegeAccount")
    @ResponseBody
    public CommonResult updateAccount(@RequestBody Account account) {
        account.setPassword(MD5.md5(ConstDatas.MD5_SALT+account.getPassword()));
        boolean result = accountService.updateAccount(account);
        return new CommonResult();
    }

    /**
     * API:删除用户
     */
    @DeleteMapping("/api/deleteAccount/{id}")
    @ResponseBody
    public CommonResult deleteAccount(@PathVariable int id) {
        boolean result = accountService.deleteAccount(id);
        return new CommonResult();
    }

    /**
     * API:禁用账号
     */
    @PostMapping(value="/api/disabledAccount/{id}")
    @ResponseBody
    public CommonResult disabledAccount(@PathVariable int id) {
        boolean result = accountService.disabledAccount(id);
        return new CommonResult();
    }

    /**
     * API:解禁账号
     */
    @PostMapping(value="/api/abledAccount/{id}")
    @ResponseBody
    public CommonResult abledAccount(@PathVariable int id) {
        boolean result = accountService.abledAccount(id);
        return new CommonResult();
    }


}
