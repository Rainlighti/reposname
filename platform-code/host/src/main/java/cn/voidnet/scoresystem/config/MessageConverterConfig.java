package cn.voidnet.scoresystem.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

import static cn.voidnet.scoresystem.config.JsonFilters.AllowedFieldNamesMap;

@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
@Configuration
@EnableSpringDataWebSupport
public class MessageConverterConfig extends WebMvcConfigurationSupport {

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                ObjectMapper mapper = ((MappingJackson2HttpMessageConverter) converter).getObjectMapper();
                mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
                var filterProvider = new SimpleFilterProvider();
                for (var entry : AllowedFieldNamesMap.entrySet()) {
                    filterProvider
                            .addFilter(entry.getKey(), SimpleBeanPropertyFilter
                                    .filterOutAllExcept(entry.getValue()));
                }
                mapper.setFilterProvider(filterProvider);
            }
        }
    }

    private static Class<?> getEnumType(Class<?> targetType) {
        Class enumType;
        for (enumType = targetType; enumType != null && !enumType.isEnum(); enumType = enumType.getSuperclass()) {
        }
        return enumType;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new PageableHandlerMethodArgumentResolver());
    }

    @Override
    public FormattingConversionService mvcConversionService() {
        FormattingConversionService fcs = super.mvcConversionService();
        fcs.addConverterFactory(new ConverterFactory<String, Enum>() {
            @Override
            public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
                return new StringToEnum(getEnumType(targetType));
            }

            class StringToEnum<T extends Enum> implements Converter<String, T> {

                private final Class<T> enumType;

                StringToEnum(Class<T> enumType) {
                    this.enumType = enumType;
                }

                @Override
                public T convert(String source) {
                    if (source.isEmpty()) {
                        return null;
                    }
                    try {
                        return (T) this.enumType
                                .getDeclaredMethod("fromValue", String.class)
                                .invoke(null, source.trim());

//                        return (T) enumType
//                                .getDeclaredConstructor(String.class,int.class,String.class)
//                                .newInstance()
//                                .fromValue(source);
                    } catch (Exception e) {
                        log.error("can't convert this type of enum");
                        throw new EnumDeseriallizationException();
                    }

                }
            }
        });
        return fcs;
    }
}
