package zipbap.global.domain.recipe

import jakarta.persistence.*
import zipbap.global.domain.base.BaseEntity
import zipbap.global.domain.category.cookingtime.CookingTime
import zipbap.global.domain.category.cookingtype.CookingType
import zipbap.global.domain.category.headcount.Headcount
import zipbap.global.domain.category.level.Level
import zipbap.global.domain.category.mainingredient.MainIngredient
import zipbap.global.domain.category.method.Method
import zipbap.global.domain.category.mycategory.MyCategory
import zipbap.global.domain.category.situation.Situation
import zipbap.global.domain.user.User

@Entity
@Table(name = "recipe", indexes = [Index(columnList = "user_id")])
class Recipe(

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        val user: User,

        @Column(length = 200, nullable = true)
        var thumbnail: String? = null,

        @Column(nullable = true, length = 100)
        var title: String? = null,

        @Column(nullable = true, length = 100)
        var subtitle: String? = null,

        @Column(nullable = true, length = 300)
        var introduction: String? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "my_category_id", nullable = true)
        var myCategory: MyCategory? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_cooking_type_id", nullable = true)
        var cookingType: CookingType? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_cooking_situation_id", nullable = true)
        var situation: Situation? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_main_ingredient_id", nullable = true)
        var mainIngredient: MainIngredient? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_method_id", nullable = true)
        var method: Method? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_headcount_id", nullable = true)
        var headcount: Headcount? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_cooking_time_id", nullable = true)
        var cookingTime: CookingTime? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_level_id", nullable = true)
        var level: Level? = null,

        @Column(name = "ingredient_info", nullable = true, length = 1000)
        var ingredientInfo: String? = null,

        @Column(name = "video", length = 200)
        var video: String? = null,

        @Column(name = "kick", nullable = true, length = 300)
        var kick: String? = null,

        @Column(name = "is_private", nullable = false)
        var isPrivate: Boolean = true,

        @Column(name = "view_count", nullable = false)
        var viewCount: Long = 0,

        @Enumerated(EnumType.STRING)
        @Column(name = "recipe_status", nullable = false)
        var recipeStatus: RecipeStatus = RecipeStatus.TEMPORARY,

        @Id
        @Column(name = "id", length = 40)
        val id: String // RC-{userId}-{sequence}

) : BaseEntity() {

}
