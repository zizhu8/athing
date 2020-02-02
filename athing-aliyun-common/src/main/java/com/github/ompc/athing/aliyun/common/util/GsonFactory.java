package com.github.ompc.athing.aliyun.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

/**
 * Gson工厂
 */
public class GsonFactory {

    /**
     * Alink协议：{@link Date}采用{@link String}型的{@code long}表示，取值为时间戳
     */
    private static final TypeAdapter<Date> dateTypeAdapterForAliyun = new TypeAdapter<Date>() {
        @Override
        public void write(JsonWriter out, Date value) throws IOException {
            out.value(String.valueOf(value.getTime()));
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            return new Date(in.nextLong());
        }
    };

    /**
     * Alink协议：{@link Boolean}和{@code boolean}采用{@code int}型的数字表示，取值为0和1
     */
    private static final TypeAdapter<Boolean> booleanTypeAdapterForAliyun = new TypeAdapter<Boolean>() {
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            out.value(value ? 1 : 0);
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            return in.nextInt() != 0;
        }
    };

    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Date.class, dateTypeAdapterForAliyun)
            .registerTypeAdapter(Boolean.class, booleanTypeAdapterForAliyun)
            .registerTypeAdapter(boolean.class, booleanTypeAdapterForAliyun)
            .create();

    /**
     * 获取Gson
     *
     * @return gson
     */
    public static Gson getGson() {
        return gson;
    }


}
