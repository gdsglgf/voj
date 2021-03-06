package com.trunkshell.voj.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.trunkshell.voj.web.model.User;
import com.trunkshell.voj.web.model.UserGroup;
import com.trunkshell.voj.web.service.LanguageService;
import com.trunkshell.voj.web.service.UserService;
import com.trunkshell.voj.web.util.LocaleUtils;

/**
 * 处理应用程序公共的请求.
 * 
 * @author Xie Haozhe
 */
@Controller
@RequestMapping(value = "/")
public class DefaultController {
    /**
     * 显示应用程序的首页.
     * @param request - HttpRequest对象
     * @param response - HttpResponse对象
     * @return 一个包含首页内容的ModelAndView对象
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView indexView(
            HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("index");
        return view;
    }
    
    /**
     * 显示使用条款页面.
     * @param request - HttpRequest对象
     * @param response - HttpResponse对象
     * @return 一个包含使用条款页面内容的ModelAndView对象
     */
    @RequestMapping(value = "/terms", method = RequestMethod.GET)
    public ModelAndView termsView(
            HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("misc/terms");
        return view;
    }

    /**
     * 显示隐私页面.
     * @param request - HttpRequest对象
     * @param response - HttpResponse对象
     * @return 一个包含隐私页内面容的ModelAndView对象
     */
    @RequestMapping(value = "/privacy", method = RequestMethod.GET)
    public ModelAndView privacyView(
            HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("misc/privacy");
        return view;
    }

    /**
     * 显示评测机信息页面.
     * @param request - HttpRequest对象
     * @param response - HttpResponse对象
     * @return 一个包含评测机信息页面内容的ModelAndView对象
     */
    @RequestMapping(value = "/judgers", method = RequestMethod.GET)
    public ModelAndView judgersView(
            HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("misc/judgers");
        view.addObject("languages", languageService.getAllLanguages());
        return view;
    }
    
    /**
     * 获取评测机列表.
     * @param offset - 当前加载评测机的UID
     * @param request - HttpRequest对象
     * @return 一个包含评测机列表信息的Map<String, Object>对象
     */
    @RequestMapping(value = "/getJudgers.action", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getJudgersAction(
            @RequestParam(value = "startIndex", required = false, defaultValue = "0") long offset,
            HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        UserGroup userGroup = userService.getUserGroupUsingSlug("judgers");
        List<User> judgers = userService.getUserUsingUserGroup(userGroup, offset, JUDGERS_PER_REQUEST);
        
        result.put("isSuccessful", judgers != null && !judgers.isEmpty());
        result.put("judgers", judgers);
        return result;
    }
    
    /**
     * 显示帮助页面.
     * @param request - HttpRequest对象
     * @param response - HttpResponse对象
     * @return 一个包含帮助页面内容的ModelAndView对象
     */
    @RequestMapping(value = "/help", method = RequestMethod.GET)
    public ModelAndView helpView(
            HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("misc/help");
        return view;
    }
    
    /**
     * 显示关于页面.
     * @param request - HttpRequest对象
     * @param response - HttpResponse对象
     * @return 一个包含关于页面内容的ModelAndView对象
     */
    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public ModelAndView aboutView(
            HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("misc/about");
        return view;
    }
    
    /**
     * 显示语言切换的页面.
     * @param request - HttpRequest对象
     * @param response - HttpResponse对象
     * @return 一个包含语言切换页面内容的ModelAndView对象
     */
    @RequestMapping(value = "/worldwide", method = RequestMethod.GET)
    public ModelAndView worldwideView(
            @RequestParam(value = "forward", required = false, defaultValue = "") String forwardUrl,
            HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("misc/worldwide");
        view.addObject("forwardUrl", forwardUrl);
        return view;
    }

    /**
     * 处理用户切换语言的请求.
     * @param language - 需要切换的语言代码
     * @param request - HttpRequest对象
     * @param response - HttpResponse对象
     * @return 语言切换操作结果的HashMap<String, Boolean>对象
     */
    @RequestMapping(value = "/worldwide.action", method = RequestMethod.GET)
    public @ResponseBody Map<String, Boolean> localizationAction(
            @RequestParam(value = "language", required = true) String language,
            HttpServletRequest request, HttpServletResponse response) {
        LocaleUtils.setLocale(request, response, language);
        
        Map<String, Boolean> result = new HashMap<String, Boolean>(2, 1);
        result.put("isSuccessful", true);
        return result;
    }
    
    /**
     * 对于所有未正常映射URL的页面, 显示页面未找到.
     * @param request - HttpRequest对象
     * @param response - HttpResponse对象
     * @return 返回一个包含异常信息的ModelAndView对象
     */
    @RequestMapping(value = "/{(?!assets|druid).*$}", method = RequestMethod.GET)
    public ModelAndView notFoundView(
            HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("errors/404");
        return view;
    }
    
    /**
     * 显示升级浏览器页面.
     * @param request - HttpRequest对象
     * @param response - HttpResponse对象
     * @return 一个包含升级浏览器页面内容的ModelAndView对象
     */
    @RequestMapping(value = "/not-supported", method = RequestMethod.GET)
    public ModelAndView notSupportedView(
            HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("misc/not-supported");
        return view;
    }
    
    /**
     * 每次加载评测机的数量.
     */
    private static final int JUDGERS_PER_REQUEST = 10;
    
    /**
     * 自动注入的UserService对象.
     * 用于获取评测机页面的评测机列表.
     */
    @Autowired
    private UserService userService;
    
    /**
     * 自动注入的LanguageService对象.
     * 用于获取评测机页面的编译命令.
     */
    @Autowired
    private LanguageService languageService;
}
