package study.ywork.integration.cache;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CacheDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(CacheConfig.class);
        EmployeeService employeeService = ctx.getBean(EmployeeService.class);
        System.out.println("-- 通过ID获取员工信息 --");
        Employee employee = employeeService.getEmployeeById(10);
        System.out.println(employee);

        System.out.println("-- 又通过相同ID获取员工信息 --");
        employee = employeeService.getEmployeeById(10);
        System.out.println(employee);

        System.out.println("-- 通过不同ID获取员工信息 --");
        employee = employeeService.getEmployeeById(11);
        System.out.println(employee);

        System.out.println("-- 通过名字获取员工信息 --");
        employee = employeeService.getEmployeeByName("tt");
        System.out.println(employee);

        System.out.println("-- 通过相同名字获取员工信息 --");
        employee = employeeService.getEmployeeByName("tt");
        System.out.println(employee);
        
        employeeService.clearEmployeeById(10);
        CacheUtils.printNativeCache(ctx);
        ctx.close();
    }
}
