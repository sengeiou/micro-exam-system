package com.demo.controller;

import com.demo.common.ConstDatas;
import com.demo.dto.CommonResult;
import com.demo.model.*;
import com.demo.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Controller
public class DefaultController {

    private static Log LOG = LogFactory.getLog(DefaultController.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private ContestService contestService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ReplyService replyService;

    /**
     * 首页
     */
    @GetMapping(value="/")
    public String home(HttpServletRequest request, Model model, Account currentAccount) {
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        return "home";
    }

    /**
     * 在线考试列表页
     */
    @GetMapping(value="/contest/index")
    public String contestIndex(HttpServletRequest request,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               Model model, Account currentAccount) {
        contestService.updateStateToStart();
        contestService.updateStateToEnd();
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        Map<String, Object> data = contestService.getContests(page, ConstDatas.contestPageSize);
        model.addAttribute(ConstDatas.DATA, data);
        return "contest/index";
    }

    /**
     * 在线考试页
     */
    @GetMapping(value="/contest/{contestId}")
    public String contestDetail(HttpServletRequest request,
                                @PathVariable("contestId") int contestId,
                                Model model, Account currentAccount) {
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        Contest contest = contestService.getContestById(contestId);
        if (currentAccount == null || contest.getStartTime().getTime() > System.currentTimeMillis()
                || contest.getEndTime().getTime() < System.currentTimeMillis()) {
            return "redirect:/contest/index";
        }
        List<Question> questions = questionService.getQuestionsByContestId(contestId);
        for (Question question : questions) {
            question.setAnswer("");
        }
        Map<String, Object> data = new ConcurrentHashMap<>();
        data.put("contest", contest);
        data.put("questions", questions);
        model.addAttribute(ConstDatas.DATA, data);
        return "contest/detail";
    }

    /**
     * 题库中心页
     */
    @GetMapping(value="/problemset/list")
    public String problemSet(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int page, Model model, Account currentAccount) {
        Map<String, Object> data = subjectService.getSubjects(page, ConstDatas.subjectPageSize);
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        model.addAttribute(ConstDatas.DATA, data);
        return "problem/problemset";
    }

    /**
     * 题目列表页
     */
    @GetMapping(value="/problemset/{problemsetId}/problems")
    public String problemList(HttpServletRequest request,
                              @PathVariable("problemsetId") Integer problemsetId,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "content", defaultValue = "") String content,
                              @RequestParam(value = "difficulty", defaultValue = "0") int difficulty,
                              Model model, Account currentAccount) {
        Map<String, Object> data = questionService.getQuestionsByProblemsetIdAndContentAndDiffculty(page, ConstDatas.questionPageSize,
                problemsetId, content, difficulty);
        Subject subject = subjectService.getSubjectById(problemsetId);
        data.put("subject", subject);
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        model.addAttribute(ConstDatas.DATA, data);
        return "problem/problemlist";
    }

    /**
     * 题目详情页
     */
    @GetMapping(value="/problemset/{problemsetId}/problem/{problemId}")
    public String problemDetail(HttpServletRequest request,
                                @PathVariable("problemsetId") Integer problemsetId,
                                @PathVariable("problemId") Integer problemId,
                                Model model, Account currentAccount) {
        Map<String, Object> data = new HashMap<>();
        Question question = questionService.getQuestionById(problemId);
        Subject subject = subjectService.getSubjectById(problemsetId);
        data.put("question", question);
        data.put("subject", subject);
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        model.addAttribute(ConstDatas.DATA, data);
        return "problem/problemdetail";
    }

    /**
     * 讨论区首页
     */
    @GetMapping(value="/discuss")
    public String discuss(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int page, Model model, Account currentAccount) {
        Map<String, Object> data = postService.getPosts(page, ConstDatas.postPageSize);
        List<Post> posts = (List<Post>) data.get("posts");
        Set<Integer> authorIds = posts.stream().map(Post::getAuthorId).collect(Collectors.toCollection(HashSet::new));
        List<Account> authors = accountService.getAccountsByIds(authorIds);
        Map<Integer, Account> id2author = authors.stream().
                collect(Collectors.toMap(Account::getId, account -> account));
        for (Post post : posts) {
            post.setAuthor(id2author.get(post.getAuthorId()));
        }
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        model.addAttribute(ConstDatas.DATA, data);
        return "discuss/discuss";
    }

    /**
     * 帖子详情页
     */
    @GetMapping(value="/discuss/{postId}")
    public String discussDetail(HttpServletRequest request, @PathVariable("postId") Integer postId, Model model, Account currentAccount) {
        Map<String, Object> data = new HashMap<>();
        Post post = postService.getPostById(postId);
        Account author = accountService.getAccountById(post.getAuthorId());
        post.setAuthor(author);
        data.put("post", post);

        List<Comment> comments = commentService.getCommentsByPostId(postId);
        List<Reply> replies = replyService.getReliesByPostId(postId);
        Set<Integer> userIds = new HashSet<>();
        for (Comment comment : comments) {
            comment.setReplies(new ArrayList<>());
            userIds.add(comment.getUserId());
        }
        for (Reply reply : replies) {
            userIds.add(reply.getUserId());
            userIds.add(reply.getAtuserId());
        }
        List<Account> users = accountService.getAccountsByIds(userIds);
        Map<Integer, Account> id2user = users.stream().
                collect(Collectors.toMap(Account::getId, account -> account));
        for (Comment comment : comments) {
            comment.setUser(id2user.get(comment.getUserId()));
        }
        for (Reply reply : replies) {
            reply.setUser(id2user.get(reply.getUserId()));
            if (reply.getAtuserId() != 0) {
                reply.setAtuser(id2user.get(reply.getAtuserId()));
            }
        }
        Map<Integer, Comment> id2comment = comments.stream().
                collect(Collectors.toMap(Comment::getId, comment -> comment));
        for (Reply reply : replies) {
            Comment comment = id2comment.get(reply.getCommentId());
            comment.getReplies().add(reply);
        }
        data.put("comments", comments);
        if (currentAccount != null){
            data.put("userId", currentAccount.getId());
        } else {
            data.put("userId", 0);
        }

        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        model.addAttribute(ConstDatas.DATA, data);
        return "discuss/discussDetail";
    }

    /**
     * 发布帖子页
     */
    @GetMapping(value="/discuss/post")
    public String postDiscuss(HttpServletRequest request, Model model, Account currentAccount) {
        Map<String, Object> data = new HashMap<>();
        data.put("authorId", currentAccount.getId());
        model.addAttribute(ConstDatas.CURRENT_ACCOUNT, currentAccount);
        model.addAttribute(ConstDatas.DATA, data);
        return "discuss/postDiscuss";
    }

    /**
     * 获取服务器端时间,防止用户篡改客户端时间提前参与考试
     *
     * @return 时间的json数据
     */
    @GetMapping(value = "/time/now")
    @ResponseBody
    public CommonResult time() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return new CommonResult().setData(localDateTime);
    }

}
