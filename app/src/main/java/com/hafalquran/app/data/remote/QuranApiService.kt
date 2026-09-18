package com.hafalquran.app.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface QuranApiService {

    @GET("surah")
    suspend fun getSurahList(): QuranApiResponse<List<SurahDto>>

    // Endpoint yang mengembalikan 3 edisi sekaligus: Arab Utsmani, Terjemahan Indonesia Kemenag, dan Audio Murottal Mishary Alafasy
    @GET("surah/{number}/editions/quran-uthmani,id.indonesian,ar.alafasy")
    suspend fun getSurahWithAyahs(@Path("number") number: Int): QuranApiResponse<List<SurahEditionDto>>

    companion object {
        private const val BASE_URL = "https://api.alquran.cloud/v1/"

        fun create(): QuranApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QuranApiService::class.java)
        }
    }
}

// DTO Models
data class QuranApiResponse<T>(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: T
)

data class SurahDto(
    @SerializedName("number") val number: Int,
    @SerializedName("name") val nameArabic: String,
    @SerializedName("englishName") val englishName: String,
    @SerializedName("englishNameTranslation") val englishNameTranslation: String,
    @SerializedName("numberOfAyahs") val numberOfAyahs: Int,
    @SerializedName("revelationType") val revelationType: String
)

data class SurahEditionDto(
    @SerializedName("number") val number: Int,
    @SerializedName("name") val nameArabic: String,
    @SerializedName("englishName") val englishName: String,
    @SerializedName("edition") val edition: EditionInfo,
    @SerializedName("ayahs") val ayahs: List<AyahEditionDto>
)

data class EditionInfo(
    @SerializedName("identifier") val identifier: String,
    @SerializedName("language") val language: String,
    @SerializedName("name") val name: String,
    @SerializedName("englishName") val englishName: String,
    @SerializedName("type") val type: String
)

data class AyahEditionDto(
    @SerializedName("number") val numberInQuran: Int,
    @SerializedName("numberInSurah") val numberInSurah: Int,
    @SerializedName("text") val text: String,
    @SerializedName("audio") val audio: String?
)
