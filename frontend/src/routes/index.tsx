import { SignInButton } from "@/components/principal/sign-in";
import { createFileRoute } from "@tanstack/react-router";
import { CheckCircle, DollarSign, MessageSquare } from "lucide-react";

export const Route = createFileRoute("/")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <>
      {/* Header */}
      <header className="border-border bg-background/95 sticky top-0 z-40 w-full border-b backdrop-blur">
        <div className="container mx-auto flex h-16 items-center justify-between px-4">
          <div className="text-primary text-xl font-semibold">
            Sber Teambuilding
          </div>
          <SignInButton />
        </div>
      </header>

      <div className="container mx-auto px-4 py-8 md:py-16">
        {/* Hero Section */}
        <section className="mb-16 text-center">
          <h1 className="text-primary mb-4 text-4xl font-bold md:text-6xl">
            Sber Teambuilding
          </h1>
          <p className="text-muted-foreground mx-auto max-w-2xl text-xl">
            Удобное приложение для организации командных мероприятий и
            распределения расходов
          </p>
        </section>

        {/* Problem Section */}
        <section className="mx-auto mb-16 max-w-3xl">
          <h2 className="text-primary mb-6 text-2xl font-bold md:text-3xl">
            Какую проблему решаем?
          </h2>
          <div className="bg-card text-card-foreground rounded-lg p-6 shadow-sm">
            <p className="mb-4">
              Команда Сбера часто выезжает на "тимбилдинг". В ходе тимбилдинга
              происходят разные траты, разные люди ответственны за подбор разных
              вещей.
            </p>

            <div className="bg-muted mt-6 rounded-md p-4">
              <h3 className="mb-2 font-medium">Пример: поездка в баню</h3>
              <ul className="space-y-2">
                <li className="flex items-start">
                  <span className="text-primary mr-2">•</span>
                  <span>Саша заказывает баню</span>
                </li>
                <li className="flex items-start">
                  <span className="text-primary mr-2">•</span>
                  <span>Дима покупает пиво</span>
                </li>
                <li className="flex items-start">
                  <span className="text-primary mr-2">•</span>
                  <span>Сергей заправляет свою машину и везет всех</span>
                </li>
              </ul>
            </div>

            <p className="text-muted-foreground mt-4">
              В текущем решении людям нужно составлять Excel и это неудобно
            </p>
          </div>
        </section>

        {/* Solution Section */}
        <section className="mx-auto mb-16 max-w-3xl">
          <h2 className="text-primary mb-6 text-2xl font-bold md:text-3xl">
            Как мы решаем эту проблему?
          </h2>
          <div className="bg-card text-card-foreground rounded-lg p-6 shadow-sm">
            <p className="mb-4">
              Sber Teambuilding предлагает единую платформу для организации
              мероприятий и справедливого распределения расходов:
            </p>

            <div className="mt-6 space-y-4">
              <div className="flex items-start gap-3">
                <div className="bg-primary/10 text-primary flex aspect-square h-10 w-10 items-center justify-center rounded-full">
                  1
                </div>
                <div>
                  <h3 className="mb-1 text-lg font-medium">
                    Удобное управление задачами
                  </h3>
                  <p className="text-muted-foreground">
                    Легко назначайте ответственных за покупки и организацию.
                    Больше никаких потерянных списков и забытых договоренностей.
                  </p>
                </div>
              </div>

              <div className="flex items-start gap-3">
                <div className="bg-primary/10 text-primary flex aspect-square h-10 w-10 items-center justify-center rounded-full">
                  2
                </div>
                <div>
                  <h3 className="mb-1 text-lg font-medium">
                    Прозрачный учет расходов
                  </h3>
                  <p className="text-muted-foreground">
                    Каждый участник видит итоговую смету и свою долю расходов.
                    Справедливое распределение затрат без лишних споров.
                  </p>
                </div>
              </div>

              <div className="flex items-start gap-3">
                <div className="bg-primary/10 text-primary flex aspect-square h-10 w-10 items-center justify-center rounded-full">
                  3
                </div>
                <div>
                  <h3 className="mb-1 text-lg font-medium">
                    Эффективная коммуникация
                  </h3>
                  <p className="text-muted-foreground">
                    Встроенный чат позволяет обсуждать детали мероприятия в
                    одном месте, не переключаясь между разными приложениями.
                  </p>
                </div>
              </div>
            </div>

            <div className="bg-accent/10 mt-6 rounded-md p-4">
              <p className="font-medium">
                Забудьте о неудобных таблицах Excel и путанице в мессенджерах.
                Теперь все данные о тимбилдинге в одном месте.
              </p>
            </div>
          </div>
        </section>

        {/* Features Section */}
        <section className="mb-16">
          <h2 className="text-primary mb-8 text-center text-2xl font-bold md:text-3xl">
            Возможности продукта
          </h2>

          <div className="grid grid-cols-1 gap-8 md:grid-cols-3">
            {/* Task Page */}
            <div className="bg-card text-card-foreground border-border rounded-lg border p-6 shadow-sm">
              <div className="mb-3 flex items-center gap-2">
                <CheckCircle className="text-primary h-5 w-5" />
                <h3 className="text-xl font-semibold">Задачи</h3>
              </div>
              <p className="text-muted-foreground mb-4">
                На этой странице пользователи могут просматривать разные задачи,
                создавать задачи и перемещать между ними.
              </p>
            </div>

            {/* Budget Page */}
            <div className="bg-card text-card-foreground border-border rounded-lg border p-6 shadow-sm">
              <div className="mb-3 flex items-center gap-2">
                <DollarSign className="text-primary h-5 w-5" />
                <h3 className="text-xl font-semibold">Смета</h3>
              </div>
              <p className="text-muted-foreground mb-4">
                На этой странице пользователь может просматривать итоговую смету
                для мероприятия. Также пользователи могут отслеживать, какую
                сумму задолжал каждый участник (Splitwise).
              </p>
            </div>

            {/* Chat */}
            <div className="bg-card text-card-foreground border-border rounded-lg border p-6 shadow-sm">
              <div className="mb-3 flex items-center gap-2">
                <MessageSquare className="text-primary h-5 w-5" />
                <h3 className="text-xl font-semibold">Чат</h3>
              </div>
              <p className="text-muted-foreground mb-4">
                С помощью чата пользователи могут общаться друг с другом.
              </p>
            </div>
          </div>
        </section>

        {/* CTA Section */}
        <section className="text-center">
          <div className="bg-accent/10 mx-auto max-w-2xl rounded-lg px-4 py-8">
            <h2 className="text-primary mb-4 text-2xl font-bold">
              Начните использовать прямо сейчас
            </h2>
            <p className="text-muted-foreground mb-6">
              Организуйте свой следующий тимбилдинг быстро и без лишних хлопот
            </p>
            <button className="bg-primary text-primary-foreground hover:bg-primary/90 rounded-md px-6 py-3 font-medium">
              Начать работу
            </button>
          </div>
        </section>
      </div>
    </>
  );
}
