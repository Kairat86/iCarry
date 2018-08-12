package zig.i.carry.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = OrderAd::class, name = "orderAd"),
        JsonSubTypes.Type(value = OfferAd::class, name = "offerAd")
)
abstract class Ad : Serializable {

    @JsonProperty("countryFrom")
    lateinit var countryFrom: String
    @JsonProperty("countryTo")
    lateinit var countryTo: String
    @JsonProperty("cityFrom")
    lateinit var cityFrom: String
    @JsonProperty("cityTo")
    lateinit var cityTo: String
    @JsonProperty("description")
    lateinit var description: String
    @JsonProperty("userLogin")
    lateinit var userLogin: String
    @JsonProperty("price")
    lateinit var price: String
    @JsonProperty("contacts")
    lateinit var contacts: List<Contact>
    @JsonProperty("createDate")
    var createDate: Long? = null
    @JsonProperty("currency")
    lateinit var currency: String
}