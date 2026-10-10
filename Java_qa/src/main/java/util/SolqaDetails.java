package util;
/** *********************************************************************
Purpose:		Enum for Product Name
File Name:		Product.Java
Suite Name:		Framework File
Module Name:	NA
@author			akarhadk
@version		1.0
@since			2025-04-18
Comments:		
 ********************************************************************** */
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface SolqaDetails{
    String authorName() default "author";
    String JiraID() default "JiraID";
    String TTID() default "TT-ID";
    Product[] Products() default {Product.DSOM};
}