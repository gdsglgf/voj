package com.trunkshell.voj.web.aspect;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.trunkshell.voj.web.model.Option;
import com.trunkshell.voj.web.model.User;
import com.trunkshell.voj.web.service.OptionService;
import com.trunkshell.voj.web.service.SubmissionService;
import com.trunkshell.voj.web.service.UserService;
import com.trunkshell.voj.web.util.LocaleUtils;

/**
 * 视图的切面类.
 * 在加载页面前加载已登录用户的个人信息及答题情况. 
 * @author Xie Haozhe
 */
@Aspect
public class ViewAspect {
    /**
     * 加载已登录用户的个人信息及答题情况.
     * @param proceedingJoinPoint - ProceedingJoinPoint对象
     * @param request - HttpRequest对象
     * @param response - HttpResponse对象
     * @return 一个包含预期视图的ModelAndView对象
     * @throws Throwable - ResourceNotFound异常
     */
    @Around(value = "execution(* com.trunkshell.voj.web.controller.*.*View(..)) && args(.., request, response)")
    public ModelAndView getUserProfile(ProceedingJoinPoint proceedingJoinPoint, 
            HttpServletRequest request, HttpServletResponse response) throws Throwable {
        ModelAndView view = null;
        HttpSession session = request.getSession();
        
        view = (ModelAndView) proceedingJoinPoint.proceed();
        view.addAllObjects(getSystemOptions());
        view.addObject("language", getUserLanguage(request, response));
        
        boolean isLoggedIn = isLoggedIn(session);
        if ( isLoggedIn ) {
            long userId = (Long)session.getAttribute("uid");
            User user = userService.getUserUsingUid(userId);
            
            view.addObject("isLogin", isLoggedIn)
                .addObject("myProfile", user)
                .addObject("mySubmissionStats", submissionService.getSubmissionStatsOfUser(userId));
        }
        return view;
    }
    
    /**
     * 检查用户是否已经登录.
     * @param session - HttpSession 对象
     * @return 用户是否已经登录
     */
    private boolean isLoggedIn(HttpSession session) {
        Boolean isLoggedIn = (Boolean)session.getAttribute("isLoggedIn");
        if ( isLoggedIn == null || !isLoggedIn.booleanValue() ) {
            return false;
        }
        return true;
    }
    
    /**
     * 加载系统定义的选项(Option).
     * @return 包含系统定义选项的键值对列表
     */
    private Map<String, String> getSystemOptions() {
        List<Option> options = optionService.getAutoloadOptions();
        Map<String, String> optionMap = new HashMap<String, String>();
        
        for ( Option option : options  ) {
            String key = option.getOptionName();
            String value = option.getOptionValue();
            optionMap.put(key, value);
        }
        return optionMap;
    }
    
    /**
     * 获取当前用户的显示语言.
     * @param request - HttpRequest对象
     * @param response - HttpResponse对象
     * @return 当前用户显示语言的唯一英文缩写
     */
    private String getUserLanguage(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Object languageAttribute = session.getAttribute("language");
        
        if ( languageAttribute == null ) {
            String preferNaturalLanguage = getPreferNaturalLanguage(request, response);
            LocaleUtils.setLocale(request, response, preferNaturalLanguage);
            return preferNaturalLanguage;
        }
        return (String) languageAttribute;
    }
    
    /**
     * 根据用户浏览器语言和系统支持的语言推荐默认语言.
     * @param request - HttpRequest对象
     * @param response - HttpResponse对象
     * @return 推荐语言的代码(例如zh_CN)
     */
    private String getPreferNaturalLanguage(HttpServletRequest request, HttpServletResponse response) {
        final String DEFAULT_LANGUAGE = "en_US";
        final String[] supportedLanguages = { "en_US", "zh_CN" };
        Locale browserLocale = getBrowserLocale(request);
        
        for ( String supportedLanguage : supportedLanguages ) {
            Locale supportLanguageLocale = LocaleUtils.getLocaleOfLanguage(supportedLanguage);
            if ( supportLanguageLocale.getLanguage().equals(browserLocale.getLanguage()) ) {
                return supportedLanguage;
            }
        }
        return DEFAULT_LANGUAGE;
    }
    
    /**
     * 根据浏览器语言获取用户所在地区.
     * @param request - HttpRequest对象
     * @return 一个包含用户所在地区信息的Locale对象
     */
    private Locale getBrowserLocale(HttpServletRequest request) {
        Locale locale = request.getLocale();
        return locale;
    }
    
    /**
     * 自动注入的UserService对象.
     */
    @Autowired
    private UserService userService;
    
    /**
     * 自动注入的SubmissionService对象.
     */
    @Autowired
    private SubmissionService submissionService;
    
    /**
     * 自动注入的OptionService对象.
     */
    @Autowired
    private OptionService optionService;
}
