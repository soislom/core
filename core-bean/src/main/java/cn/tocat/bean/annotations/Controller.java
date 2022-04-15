package cn.tocat.bean.annotations;
import java.lang.annotation.*;

/**
 * 类 名: MyService
 * 描 述: 自定义注解 -- 访问控制层 -- 定义在类、接口、枚举上的
 * 作 者: 黄加耀
 * 创 建: 2019/3/16 : 17:12
 * 邮 箱: huangjy19940202@gmail.com
 *
 * @author: jiaYao
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface Controller {

}