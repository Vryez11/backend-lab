package com.vryez.backendlab.lab02;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Lab02AcceptanceTest {

    @Test
    @DisplayName("완료조건 1: 스프링 없이 생성해도 NPE 없이 할인 금액을 반환하고 저장까지 된다")
    void 스프링_없이_생성해서_주문() {
        MemoryOrderRepository orderRepository = new MemoryOrderRepository();
        OrderService orderService = new OrderService(new RateDiscountPolicy(), orderRepository);

        int result = orderService.order(1L, 10000);

        assertThat(result).isEqualTo(9000);
        assertThat(orderRepository.findLast(1L)).isEqualTo(9000);
    }

    @Test
    @DisplayName("완료조건 3·4: 의존성 필드는 모두 final이고 필드 @Autowired가 없다")
    void 의존성_필드는_final이고_필드주입이_아니다() throws Exception {
        for (String name : new String[]{"discountPolicy", "orderRepository"}) {
            Field field = OrderService.class.getDeclaredField(name);
            assertThat(Modifier.isFinal(field.getModifiers()))
                    .as("%s 필드가 final인가", name).isTrue();
            assertThat(Arrays.stream(field.getAnnotations())
                    .noneMatch(a -> a.annotationType().getSimpleName().equals("Autowired")))
                    .as("%s 필드에 @Autowired가 없는가", name).isTrue();
        }
    }

    @Test
    @DisplayName("완료조건 3: 의존성을 재설정하는 public setter가 없다")
    void 의존성_setter가_없다() {
        boolean hasSetter = Arrays.stream(OrderService.class.getMethods())
                .anyMatch(m -> m.getName().startsWith("set"));
        assertThat(hasSetter).as("public set* 메서드가 없는가").isFalse();
    }

    @Test
    @DisplayName("완료조건 4: 두 의존성을 모두 받는 생성자가 정확히 1개 존재한다")
    void 두_의존성을_받는_생성자_1개() {
        Constructor<?>[] constructors = OrderService.class.getDeclaredConstructors();
        assertThat(constructors).hasSize(1);
        assertThat(constructors[0].getParameterTypes())
                .containsExactlyInAnyOrder(DiscountPolicy.class, OrderRepository.class);
    }
}
