package zipbap.app.domain.recipe

import jakarta.persistence.*
import zipbap.app.domain.base.BaseEntity
import zipbap.app.domain.category.cookingtime.CookingTime
import zipbap.app.domain.category.cookingtype.CookingType
import zipbap.app.domain.category.headcount.Headcount
import zipbap.app.domain.category.level.Level
import zipbap.app.domain.category.mainingredient.MainIngredient
import zipbap.app.domain.category.method.Method
import zipbap.app.domain.category.mycategory.MyCategory
import zipbap.app.domain.category.situation.Situation
import zipbap.app.domain.user.User

@Entity
@Table(name = "recipe", indexes = [Index(columnList = "user_id")])
class Recipe(

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        val user: User,

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

        @Enumerated(EnumType.STRING)
        @Column(name = "recipe_status", nullable = false)
        var recipeStatus: RecipeStatus = RecipeStatus.TEMPORARY,

        @Id
        @Column(name = "id", length = 40)
        val id: String,  // RC-{userId}-{sequence}
) : BaseEntity() {
}
