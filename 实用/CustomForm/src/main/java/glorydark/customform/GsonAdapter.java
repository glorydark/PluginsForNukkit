package glorydark.customform;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CSDN article: <a href="https://bukecode.blog.csdn.net/article/details/78781424?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1-78781424-blog-105209396.pc_relevant_multi_platform_whitelistv2&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1-78781424-blog-105209396.pc_relevant_multi_platform_whitelistv2&utm_relevant_index=1">https://bukecode.blog.csdn.net/article/details/78781424?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1-78781424-blog-105209396.pc_relevant_multi_platform_whitelistv2&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1-78781424-blog-105209396.pc_relevant_multi_platform_whitelistv2&utm_relevant_index=1</a>
 * Glorydark added some small changes.
 */
public class GsonAdapter extends TypeAdapter<Object> {
    @Override
    public Object read(JsonReader in) throws IOException
    {
        // εεΊεε
        JsonToken token = in.peek();
        switch (token)
        {
            case BEGIN_ARRAY:

                List<Object> list = new ArrayList<>();
                in.beginArray();
                while (in.hasNext())
                {
                    list.add(read(in));
                }
                in.endArray();
                return list;

            case BEGIN_OBJECT:

                Map<String, Object> map = new HashMap<>();
                in.beginObject();
                while (in.hasNext())
                {
                    map.put(in.nextName(), read(in));
                }
                in.endObject();
                return map;

            case STRING:

                return in.nextString(); //ε°δΏ?ζΉοΌζ­€ε€θΏεutf-8εΌ

            case NUMBER:

                /*
                  ζΉεζ°ε­ηε€ηι»θΎοΌε°ζ°ε­εΌεδΈΊζ΄εδΈζ΅?ηΉεγ
                 */
                double dbNum = in.nextDouble();

                // ζ°ε­θΆθΏlongηζε€§εΌοΌθΏεζ΅?ηΉη±»ε
                if (dbNum > Long.MAX_VALUE)
                {
                    return dbNum;
                }

                // ε€ζ­ζ°ε­ζ―ε¦δΈΊζ΄ζ°εΌ
                long lngNum = (long) dbNum;
                if (dbNum == lngNum)
                {
                    return Integer.parseInt(String.valueOf(lngNum));//ε°δΏ?ζΉοΌζ­€ε€θΏεζ΄ζ°εΌ
                }else{
                    return dbNum;
                }

            case BOOLEAN:
                return in.nextBoolean();

            case NULL:
                in.nextNull();
                return null;

            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void write(JsonWriter out, Object value) {
        // εΊεεδΈε€η
    }
}
