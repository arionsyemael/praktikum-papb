package com.tifd.projectcomposed


import retrofit2.http.GET

interface ApiService {
    @GET("users/arionsyemael")
    suspend fun getGithubProfile(): GithubProfile
}