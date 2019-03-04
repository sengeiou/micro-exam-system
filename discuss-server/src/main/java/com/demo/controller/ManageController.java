package com.demo.controller;

import com.demo.common.ConstDatas;
import com.demo.exception.CommonError;
import com.demo.exception.CommonException;
import com.demo.model.*;
import com.demo.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/manage")
public class ManageController {

    private static Log LOG = LogFactory.getLog(ManageController.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private ContestService contestService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private GradeService gradeService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;

    /**
     * 管理员登录页
     */
    @GetMapping(value="/login")
    public String login(HttpServletRequest request, Model model, Account currentAccount) throws CommonException {
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        if (currentAccount == null) {
            return "manage/manage-login";
        } else if (currentAccount.getLevel()<1){
            throw new CommonException(CommonError.PERMISSION_WRONG);
        } else {
            return "redirect:contest/list";
        }
    }

    /**
     * 用户管理
     */
    @GetMapping(value="/account/list")
    public String accountList(HttpServletRequest request,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              Model model, Account currentAccount) {
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        Map<String, Object> data = accountService.getAccounts(page, ConstDatas.accountPageSize);
        model.addAttribute(ConstDatas.DATA, data);
        return "manage/manage-accountList";
    }

    /**
     * 考试管理
     */
    @GetMapping(value="/contest/list")
    public String contestList(HttpServletRequest request,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              Model model, Account currentAccount) {
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        Map<String, Object> data = contestService.getContests(page, ConstDatas.contestPageSize);
        List<Subject> subjects = subjectService.getSubjects();
        data.put("subjects", subjects);
        model.addAttribute(ConstDatas.DATA, data);
        return "manage/manage-contestBoard";
    }

    /**
     * 考试管理-查看试题
     */
    @GetMapping(value="/contest/{contestId}/problems")
    public String contestProblemList(HttpServletRequest request,
                                     @PathVariable("contestId") Integer contestId, Model model, Account currentAccount) {
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        Map<String, Object> data = new HashMap<>();
        List<Question> questions = questionService.getQuestionsByContestId(contestId);
        Contest contest = contestService.getContestById(contestId);
        data.put("questionsSize", questions.size());
        data.put("questions", questions);
        data.put("contest", contest);
        model.addAttribute(ConstDatas.DATA, data);
        return "manage/manage-editContestProblem";
    }

    /**
     * 題目管理
     */
    @GetMapping(value="/question/list")
    public String questionList(HttpServletRequest request,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "content", defaultValue = "") String content,
                               Model model, Account currentAccount) {
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        Map<String, Object> data = questionService.getQuestionsByContent(page,
                    ConstDatas.questionPageSize, content);
        List<Question> questions = (List<Question>) data.get("questions");
        List<Subject> subjects = subjectService.getSubjects();
        Map<Integer, String> subjectId2name = subjects.stream().
                collect(Collectors.toMap(Subject::getId, Subject::getName));
        for (Question question : questions) {
            question.setSubjectName(subjectId2name.
                    getOrDefault(question.getSubjectId(), "未知科目"));
        }
        data.put("subjects", subjects);
        data.put("content", content);
        model.addAttribute("data", data);
        return "manage/manage-questionBoard";
    }

    /**
     * 成绩管理-考试列表
     */
    @GetMapping(value="/result/contest/list")
    public String resultContestList(HttpServletRequest request,
                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                    Model model, Account currentAccount) {
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        Map<String, Object> data = contestService.getContests(page, ConstDatas.contestPageSize);
        List<Subject> subjects = subjectService.getSubjects();
        data.put("subjects", subjects);
        model.addAttribute(ConstDatas.DATA, data);
        return "manage/manage-resultContestBoard";

    }
    /**
     * 成绩管理-考试列表-学生成绩列表
     */
    @GetMapping(value="/result/contest/{contestId}/list")
    public String resultStudentList(HttpServletRequest request,
                                    @PathVariable("contestId") int contestId,
                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                    Model model, Account currentAccount) {
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        Map<String, Object> data = new HashMap<>();
        List<Grade> grades = gradeService.getGradesByContestId(contestId);
        Contest contest = contestService.getContestById(contestId);
        List<Question> questions = questionService.getQuestionsByContestId(contestId);
        List<Integer> studentIds = grades.stream().map(Grade::getStudentId).collect(Collectors.toList());
        List<Account> students = accountService.getAccountsByStudentIds(studentIds);
        Map<Integer, Account> id2student = students.stream().
                collect(Collectors.toMap(Account::getId, account -> account));
        for (Grade grade : grades) {
            Account student = id2student.get(grade.getStudentId());
            grade.setStudent(student);
        }
        data.put("grades", grades);
        data.put("contest", contest);
        data.put("questions", questions);
        model.addAttribute(ConstDatas.DATA, data);
        return "manage/manage-resultStudentBoard";
    }

    /**
     * 课程管理
     */
    @GetMapping(value="/subject/list")
    public String subjectList(HttpServletRequest request,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              Model model, Account currentAccount) {
        //TODO::处理
        //currentAccount = accountService.getAccountByUsername("admin");
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        Map<String, Object> data = subjectService.getSubjects(page, ConstDatas.subjectPageSize);
        model.addAttribute(ConstDatas.DATA, data);
        return "manage/manage-subjectBoard";
    }

    /**
     * 帖子管理
     */
    @GetMapping(value="/post/list")
    public String postList(HttpServletRequest request,
                           @RequestParam(value = "page", defaultValue = "1") int page,
                           Model model, Account currentAccount) {
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        Map<String, Object> data = postService.getPosts(page, ConstDatas.postPageSize);
        List<Post> posts = (List<Post>) data.get("posts");
        Set<Integer> authorIds = posts.stream().map(Post::getAuthorId).collect(Collectors.toCollection(HashSet::new));
        List<Account> authors = accountService.getAccountsByIds(authorIds);
        Map<Integer, Account> id2author = authors.stream().
                collect(Collectors.toMap(Account::getId, account -> account));
        for (Post post : posts) {
            post.setAuthor(id2author.get(post.getAuthorId()));
        }
        model.addAttribute(ConstDatas.DATA, data);
        return "manage/manage-postBoard";
    }

    /**
     * 评论管理
     */
    @GetMapping(value="/comment/list")
    public String commentList(HttpServletRequest request,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              Model model, Account currentAccount) {
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        Map<String, Object> data = commentService.getComments(page, ConstDatas.commentPageSize);
        List<Comment> comments = (List<Comment>) data.get("comments");
        Set<Integer> userIds = comments.stream().map(Comment::getUserId).collect(Collectors.toCollection(HashSet::new));
        List<Account> users = accountService.getAccountsByIds(userIds);
        Map<Integer, Account> id2user = users.stream().
                collect(Collectors.toMap(Account::getId, account -> account));
        for (Comment comment : comments) {
            comment.setUser(id2user.get(comment.getUserId()));
        }
        model.addAttribute(ConstDatas.DATA, data);
        return "manage/manage-commentBoard";
    }
}
