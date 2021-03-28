package masoumi.formularenderer.util

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import masoumi.formularenderer.data.CallError
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CallErrorJsonAdapter : JsonAdapter<CallError>() {
    private val options = JsonReader.Options.of("detail","details")

    override fun toJson(writer: JsonWriter, value: CallError?) {
        if (value == null) {
            writer.nullValue()
        } else {
            val string = value.error
            writer.value(string)
        }
    }

    override fun fromJson(reader: JsonReader): CallError? {
        var errorString : String? = null
        if (reader.peek() == JsonReader.Token.NULL) {
            return reader.nextNull<CallError>()
        }
        while (reader.hasNext()){
            if(reader.selectName(options) == -1){
                reader.skipName()
                reader.skipValue()
            }
            else{
                if(reader.peek() == JsonReader.Token.STRING){
                    errorString = reader.nextString()
                }
                else if(reader.peek() == JsonReader.Token.BEGIN_OBJECT){
                    reader.beginObject()
                    while(reader.hasNext()){
                        if(reader.selectName(options) == -1){
                            reader.skipName()
                            reader.skipValue()
                        }
                    }
                    if(reader.peek() == JsonReader.Token.STRING){
                        errorString = reader.nextString()
                    }
                }
            }
        }
        return CallError(errorString)
    }
}