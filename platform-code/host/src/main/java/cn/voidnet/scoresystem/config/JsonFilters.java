package cn.voidnet.scoresystem.config;

import java.util.Map;
import java.util.Set;

public class JsonFilters {
    public static final String OnlyEntry = "1";
    public static final String OnlyEntrySchoolInstitute = "2";
    public static final Map<String, Set<String>> AllowedFieldNamesMap =
            Map.of(
                    OnlyEntry, Set.of("id", "name"),
                    OnlyEntrySchoolInstitute, Set.of("id", "name", "school", "institute")
            );
}
