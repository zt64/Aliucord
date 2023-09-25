package com.aliucord.coreplugins.repo.filtering

internal data class Developer(var github_username: String, var ID: Int) {
    var plugin_repo_name: String? = null
    var repo_stars = 0
    override fun toString(): String = github_username
}
