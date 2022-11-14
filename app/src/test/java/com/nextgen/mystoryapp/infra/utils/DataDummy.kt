package com.nextgen.mystoryapp.infra.utils

import com.nextgen.mystoryapp.data.common.util.ResponseWrapper
import com.nextgen.mystoryapp.data.login.remote.dto.LoginResponse
import com.nextgen.mystoryapp.data.login.remote.dto.LoginResult
import com.nextgen.mystoryapp.data.story.remote.dto.StoryItem
import com.nextgen.mystoryapp.data.story.remote.dto.StoryResponse
import com.nextgen.mystoryapp.domain.login.entity.LoginEntity
import com.nextgen.mystoryapp.domain.story.entity.StoryEntity
import com.nextgen.mystoryapp.ui.camera.AddStoryFragment
object DataDummy {
    fun generateDummyListStoryItem(): List<StoryItem>{
        val storyList: MutableList<StoryItem> = arrayListOf()
        for (i in 0..100){
            val story = StoryItem(
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                "agus",
                "bla bla",
                10F,
                "$1",
                10F
            )
            storyList.add(story)
        }
        return storyList
    }
    fun generateDummyStoryEntity(): StoryEntity{
         return StoryEntity(
             "$1",
             "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
             "2022-02-22T22:22:22Z",
             "agus",
             "bla bla"
         )
    }

    fun generateDummyStoryResponse(): StoryResponse{
        return StoryResponse(
            1,
            generateDummyListStoryItem(),
            false,
            "success"
        )
    }

    fun generatePostResponse(): ResponseWrapper{
        return ResponseWrapper(
            200,
            false,
            "success"
        )
    }

    fun generateSignupResponse(): ResponseWrapper {
        return ResponseWrapper(
            200,
            false,
            "User Created"
        )
    }

    fun generateLoginEntity(): LoginEntity{
        return LoginEntity(
            "1",
            "beran",
            "1234"
        )
    }
}

