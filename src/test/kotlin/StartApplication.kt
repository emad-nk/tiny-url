import com.tinyurl.Initializer
import com.tinyurl.TinyUrlApplication
import org.springframework.boot.builder.SpringApplicationBuilder

/**
 * Entry point for starting application locally.
 */
class StartApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplicationBuilder(TinyUrlApplication::class.java)
                .profiles("local")
                .initializers(Initializer())
                .run(*args)
        }
    }
}
