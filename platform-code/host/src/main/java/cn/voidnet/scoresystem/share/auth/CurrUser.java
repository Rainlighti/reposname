package cn.voidnet.scoresystem.share.auth;

import java.lang.annotation.*;

//使用此注解时需要保证要注入的用户在权限控制的白名单中,且白名单中仅此一种用户,避免类型转换错误
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrUser {
}
