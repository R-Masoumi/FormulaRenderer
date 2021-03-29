/**
 * Sportsbook.API
 * Sportsbook API
 *
 * OpenAPI spec version: v1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
package masoumi.formularenderer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

data class CallError (
        val error: String? = null
)

@JsonClass(generateAdapter = true)
data class CallBody (
        @Json(name = "q")
        val query: String
)

@JsonClass(generateAdapter = true)
data class SuccessResult(
        val success : Boolean?,
        val checked : String?,
        val endsWithDot : Boolean?
)

@Entity
data class Formula (
        @PrimaryKey
        val formula : String,
        val hash: String? = null,
        val formatted : String? = null
)