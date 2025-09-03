package zipbap.api.domain.recipe

import jakarta.persistence.*
import zipbap.api.domain.base.BaseEntity
import zipbap.api.domain.category.cookingtime.CookingTime
import zipbap.api.domain.category.cookingtype.CookingType
import zipbap.api.domain.category.headcount.Headcount
import zipbap.api.domain.category.level.Level
import zipbap.api.domain.category.mainingredient.MainIngredient
import zipbap.api.domain.category.method.Method
import zipbap.api.domain.category.mycategory.MyCategory
import zipbap.api.domain.category.situation.Situation
import zipbap.api.domain.user.User

@Entity
@Table(name = "recipe", indexes = [
        Index(columnList = "user_id")])
class Recipe(

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        val user: User,

        @Column(nullable = false, length = 100)
        var title: String,

        @Column(nullable = false, length = 100)
        var subtitle: String,

        @Column(nullable = false, length = 300)
        var introduction: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "my_category_id", nullable = false)
        var myCategory: MyCategory,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_cooking_type_id", nullable = false)
        var cookingType: CookingType,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_cooking_situation_id", nullable = false)
        var situation: Situation,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_main_ingredient_id", nullable = false)
        var mainIngredient: MainIngredient,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_method_id", nullable = false)
        var method: Method,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_headcount_id", nullable = false)
        var headcount: Headcount,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_cooking_time_id", nullable = false)
        var cookingTime: CookingTime,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_level_id", nullable = false)
        var level: Level,

        @Column(name = "ingredient_info", nullable = false,
                length = 1000)
        var ingredientInfo: String,

        @Column(name = "video", length = 200)
        var video: String?,

        @Column(name = "kick", nullable = false,
                length = 300)
        var kick: String,

        @Column(name = "is_private", nullable = false)
        var isPrivate: Boolean,

        @Enumerated(EnumType.STRING)
        @Column(name = "recipe_status", nullable = false)
        var recipeStatus: RecipeStatus,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null
) : BaseEntity() {
}