package cn.voidnet.scoresystem.share.auth;

import cn.voidnet.scoresystem.share.user.UserType;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessControl {
    UserType[] value() default {};//如为空,则不限制用户类型

    @Deprecated(since = "现在,value为空时,则为不限制用户类型,而无需设置此项")
    boolean passAllType() default false;//只要登录即可,不限制用户类型
}
