package com.tifd.projectcomposed

import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApiService {
    @GET("users/{username}")
    suspend fun getGithubProfile(@Path("username") username: String): GithubProfile
}