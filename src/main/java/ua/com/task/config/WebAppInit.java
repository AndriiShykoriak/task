package ua.com.task.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;


public class WebAppInit implements WebApplicationInitializer{
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(MvcConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
//        DelegatingFilterProxy delegatingFilterProxy = new DelegatingFilterProxy();
//        servletContext
//                .addFilter("springSecurityFilterChain", delegatingFilterProxy)
//                .addMappingForUrlPatterns(null,false,"/*" );
        ServletRegistration.Dynamic registration =
                servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
        registration.setMultipartConfig(new MultipartConfigElement("",90000000,90000000,90000000));
    }

}
