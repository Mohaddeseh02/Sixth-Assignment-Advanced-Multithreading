package sbu.cs.CalculatePi;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.Executors;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.math.RoundingMode;

public class PiCalculator {
    // تعداد رشته‌ها برای استفاده در محاسبات
    private static final int threads = 8;

    // متد برای محاسبه مقدار پی با دقت مشخص
    public String calculate(int floatingPoint) {
        // ایجاد یک thread pool با تعداد مشخصی از رشته‌ها
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        // AtomicReference برای نگهداری نتیجه محاسبات
        AtomicReference<BigDecimal> result = new AtomicReference<>(BigDecimal.ZERO);

        // آستانه برای دقت اعشار
        BigDecimal floatingPointThreshold = BigDecimal.ONE.scaleByPowerOfTen(-floatingPoint);

        // تقسیم محاسبات بین رشته‌ها
        for (int i = 0; i < threads; i++) {
            final int threadIndex = i;
            executorService.execute(() -> {
                BigDecimal sum = BigDecimal.ZERO;
                for (int k = threadIndex; ; k += threads) {
                    // محاسبه هر عبارت از سری
                    BigDecimal term = computeTerm(k, floatingPoint);
                    sum = sum.add(term);
                    // اگر عبارت کمتر از آستانه باشد، حلقه را متوقف کنید
                    if (term.abs().compareTo(floatingPointThreshold) < 0) {
                        break;
                    }
                }
                // به‌روزرسانی نتیجه به‌صورت ایمن در برابر رشته‌ها
                synchronized (result) {
                    BigDecimal finalSum = sum;
                    result.updateAndGet(currentSum -> currentSum.add(finalSum));
                }
            });
        }
        // خاموش کردن خدمات executor
        executorService.shutdown();
        try {
            // انتظار برای اتمام همه وظایف
            if (!executorService.awaitTermination(10, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        }
        catch (InterruptedException e) {
            // رسیدگی به استثنا و خاموش کردن خدمات executor
            System.out.println(e.getMessage());
            executorService.shutdownNow();
        }
        // دریافت نتیجه و تنظیم مقیاس
        BigDecimal pi = result.get().setScale(floatingPoint, RoundingMode.HALF_DOWN);
        return pi.toString();
    }

    // متد برای محاسبه هر عبارت از سری
    private BigDecimal computeTerm(int i, int floatingPoint) {
        // محاسبه صورت عبارت
        BigDecimal numerator = BigDecimal.valueOf(4).divide(BigDecimal.valueOf(8L * i + 1), floatingPoint + 5, RoundingMode.HALF_DOWN).subtract(BigDecimal.valueOf(2).divide(BigDecimal.valueOf(8L * i + 4), floatingPoint + 5, RoundingMode.HALF_DOWN)).subtract(BigDecimal.ONE.divide(BigDecimal.valueOf(8L * i + 5), floatingPoint + 5, RoundingMode.HALF_DOWN)).subtract(BigDecimal.ONE.divide(BigDecimal.valueOf(8L * i + 6), floatingPoint + 5, RoundingMode.HALF_DOWN));
        // محاسبه مخرج عبارت
        BigDecimal denominator = BigDecimal.valueOf(16).pow(i);
        // برگرداندن عبارت
        return numerator.divide(denominator, floatingPoint + 5, RoundingMode.HALF_EVEN);
    }

    // متد اصلی برای آزمایش PiCalculator
    public static void main(String[] args) {
        PiCalculator calculator = new PiCalculator();
        // محاسبه پی با 100 رقم اعشار
        String pi = calculator.calculate(100);
        // چاپ نتیجه
        System.out.println(pi);
    }
}
