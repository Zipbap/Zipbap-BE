package support.fixture

import zipbap.global.domain.cookingorder.CookingOrder
import zipbap.global.domain.recipe.Recipe

object CookingOrderFixture {

    fun create(
        // 💡 필수: 어떤 레시피의 요리 순서인지 알아야 하므로 무조건 주입받습니다.
        recipe: Recipe,

        // 💡 Unique 제약조건(recipe_id + turn) 주의!
        // 같은 레시피에 여러 과정을 넣을 땐 반드시 turn 값을 다르게 덮어써야 합니다.
        turn: Int = 1,

        image: String? = "https://cdn.zipbap.com/default_step_image.png",
        description: String = "재료를 먹기 좋은 크기로 썰어 준비합니다.",

        id: Long? = null
    ): CookingOrder {
        return CookingOrder(
            recipe = recipe,
            image = image,
            description = description,
            turn = turn,
            id = id
        )
    }

    /**
     * 🎁 꿀팁 유틸 함수: 여러 개의 요리 과정을 한 번에 생성하고 싶을 때 사용하세요!
     * ex) CookingOrderFixture.createList(recipe = recipe, count = 3)
     */
    fun createList(recipe: Recipe, count: Int = 3): List<CookingOrder> {
        return (1..count).map { currentTurn ->
            create(
                recipe = recipe,
                turn = currentTurn, // 1부터 count까지 자동으로 순서가 매겨짐 (Unique 에러 방지)
                description = "${currentTurn}번째 요리 과정입니다."
            )
        }
    }
}