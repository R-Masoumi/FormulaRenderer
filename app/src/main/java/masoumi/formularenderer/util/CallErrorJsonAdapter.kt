package masoumi.formularenderer.util

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import masoumi.formularenderer.data.CallError

/**
 * Custom Json adapter for error object.
 * Considering how wikipedia sends the error message in detail OR detail.error.message
 * this adapter cannot be generated automatically
 */
class CallErrorJsonAdapter : JsonAdapter<CallError>() {
    private val options = JsonReader.Options.of("detail","error","message")

    override fun toJson(writer: JsonWriter, value: CallError?) {
        if (value == null) {
            throw NullPointerException("value was null! Wrap in .nullSafe() to write nullable values.")
        }
        writer.beginObject()
        writer.name("error")
        writer.value(value.error)
        writer.endObject()
    }

    override fun fromJson(reader: JsonReader): CallError {
        var errorString : String? = null
        reader.beginObject()
        while (reader.hasNext()){
            if(reader.selectName(options) == -1){
                reader.skipName()
                reader.skipValue()
            }
            else{
                //check if detail is object or string
                if(reader.peek() == JsonReader.Token.STRING){
                    errorString = reader.nextString()
                }
                else if(reader.peek() == JsonReader.Token.BEGIN_OBJECT){
                    reader.beginObject()
                    while(reader.hasNext()){
                        //find error object
                        if(reader.selectName(options) == -1){
                            reader.skipName()
                            reader.skipValue()
                        }
                        else{
                            reader.beginObject()
                            while(reader.hasNext()){
                                //find message string
                                if(reader.selectName(options) == -1){
                                    reader.skipName()
                                    reader.skipValue()
                                }
                                else{
                                    errorString = reader.nextString()
                                }
                            }
                            reader.endObject()
                        }
                    }
                    reader.endObject()
                }
            }
        }
        reader.endObject()
        return CallError(errorString)
    }
}