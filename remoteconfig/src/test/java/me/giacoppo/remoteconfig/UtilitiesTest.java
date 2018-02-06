package me.giacoppo.remoteconfig;

import com.google.gson.JsonObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertTrue;

/**
 * Created by Peppe on 06/02/2018.
 */
@RunWith(JUnit4.class)
public class UtilitiesTest {

    @Test
    public void testMerge() {

        //test simple merge
        System.out.println("Test simple merge");
        String defaultJson = "{ \"key1\": \"Default value for key 1\", \"key2\": \"Default value for key 2\" }";
        String activatedJson = "{\"key1\": \"Activated value for key 1\"}";

        String mergedJson = "{\"key1\": \"Activated value for key 1\",\"key2\": \"Default value for key 2\"}";

        JsonObject mergedRightObj = Utilities.Json.from(mergedJson, JsonObject.class);
        JsonObject mergedGeneratedObj = Utilities.Json.from(Utilities.Json.merge(defaultJson,activatedJson),JsonObject.class);

        System.out.println("Result: "+ mergedGeneratedObj.toString());
        assertTrue(mergedRightObj.equals(mergedGeneratedObj));

        //test no-replace obj
        System.out.println("Test no-replace objs");
        defaultJson = "{\"key1\":\"Default value for key 1\",\"key2\":\"Default value for key 2\",\"key3\":{\"subkey1\":\"Default value for subkey 1\",\"subkey2\":\"Default value for subkey 2\"}}";
        activatedJson = "{\"key1\":\"Activated value for key 1\",\"key2\":\"Activated value for key 2\",\"key3\":\"Activated value for key 3\"}";

        mergedJson = "{\"key1\":\"Activated value for key 1\",\"key2\":\"Activated value for key 2\",\"key3\":\"Activated value for key 3\"}";

        mergedRightObj = Utilities.Json.from(mergedJson, JsonObject.class);
        mergedGeneratedObj = Utilities.Json.from(Utilities.Json.merge(defaultJson,activatedJson),JsonObject.class);

        System.out.println("Result: "+ mergedGeneratedObj.toString());
        assertTrue(mergedRightObj.equals(mergedGeneratedObj));

        //test no-replace value
        System.out.println("Test no-replace values");
        defaultJson = "{\"key1\":\"Default value for key 1\",\"key2\":\"Default value for key 2\",\"key3\":\"Default value for key 3\"}";
        activatedJson = "{\"key1\":\"Activated value for key 1\",\"key2\":\"Activated value for key 2\",\"key3\":\"Activated value for key 3\"}";

        mergedJson = "{\"key1\":\"Activated value for key 1\",\"key2\":\"Activated value for key 2\",\"key3\":\"Activated value for key 3\"}";

        mergedRightObj = Utilities.Json.from(mergedJson, JsonObject.class);
        mergedGeneratedObj = Utilities.Json.from(Utilities.Json.merge(defaultJson,activatedJson),JsonObject.class);

        System.out.println("Result: "+ mergedGeneratedObj.toString());
        assertTrue(mergedRightObj.equals(mergedGeneratedObj));

        //test merge arrays
        System.out.println("Test merge arrays");
        defaultJson ="{\"key1\":\"Default value for key 1\",\"key2\":[\"First default item in key 2\",\"Second default item in key 2\"]}";
        activatedJson = "{\"key1\":\"Activated value for key 1\",\"key2\":[\"First activated item in key 2\",\"Second activated item in key 2\"]}";

        mergedJson = "{\"key1\":\"Activated value for key 1\",\"key2\":[\"First activated item in key 2\",\"Second activated item in key 2\"]}";

        mergedRightObj = Utilities.Json.from(mergedJson, JsonObject.class);
        mergedGeneratedObj = Utilities.Json.from(Utilities.Json.merge(defaultJson,activatedJson),JsonObject.class);

        System.out.println("Result: "+ mergedGeneratedObj.toString());
        assertTrue(!mergedRightObj.equals(mergedGeneratedObj));
    }
}