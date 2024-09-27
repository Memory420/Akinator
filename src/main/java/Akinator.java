import java.io.*;
import java.util.Scanner;

class Akinator {
    public static void main(String[] args) {
        Tree currentTree = null;
        Scanner scanner = new Scanner(System.in); // Используем один экземпляр Scanner

        // Меню выбора действия
        System.out.println("Выберите действие (введите цифру): ");
        System.out.println("1. Загрузить дерево решений по умолчанию.");
        System.out.println("2. Загрузить дерево решений из файла (tree.dat).");

        int userChoiceNumber = 0;
        while (true) {
            try {
                userChoiceNumber = Integer.parseInt(scanner.nextLine());
                if (userChoiceNumber == 1 || userChoiceNumber == 2) {
                    break;
                } else {
                    System.out.println("Пожалуйста, введите 1 или 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Пожалуйста, введите 1 или 2.");
            }
        }

        // Попытка загрузить дерево из файла или создание дефолтного дерева
        switch (userChoiceNumber) {
            case 2:
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("tree.dat"))) {
                    currentTree = (Tree) ois.readObject();
                    System.out.println("Дерево загружено...");
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Ошибка при загрузке дерева: " + e.getMessage());
                    System.out.println("Загружается дерево по умолчанию.");
                    currentTree = createDefaultTree();
                }
                break;
            case 1:
                currentTree = createDefaultTree();
                break;
        }

        Tree rootTree = currentTree; // Сохраняем корень дерева для последующего сохранения

        int questionCount = 0; // Счетчик вопросов

        // Прохождение по дереву вопросов
        while (currentTree.isQuestion()) {
            System.out.println(currentTree.getData());
            String answer = scanner.nextLine().trim().toLowerCase();
            if (answer.equals("да")) {
                currentTree = currentTree.getYes();
            } else if (answer.equals("нет")) {
                currentTree = currentTree.getNo();
            } else {
                System.out.println("Некорректный ответ. Пожалуйста, ответьте 'да' или 'нет'.");
                continue; // Повторяем вопрос
            }
            questionCount++; // Увеличиваем счетчик вопросов
        }

        // Вывод предположения
        System.out.println(currentTree.getData());

        // Проверяем, правильный ли это ответ
        System.out.println("Это правильный ответ? (да/нет)");
        String correctAnswer = scanner.nextLine().trim().toLowerCase();
        if (correctAnswer.equals("да")) {
            System.out.println("Я угадал!");
            System.out.println("Количество заданных вопросов: " + questionCount);
        } else if (correctAnswer.equals("нет")) {
            // Предлагаем завершить игру или записать новый ответ
            System.out.println("Завершить игру (1)? Записать новый ответ (2)?");
            int userChoice = 0;
            while (true) {
                try {
                    userChoice = Integer.parseInt(scanner.nextLine());
                    if (userChoice == 1 || userChoice == 2) {
                        break;
                    } else {
                        System.out.println("Пожалуйста, введите 1 или 2.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Некорректный ввод. Пожалуйста, введите 1 или 2.");
                }
            }
            if (userChoice == 1) {
                System.out.println("Завершаем...");
                saveTree(rootTree); // Сохраняем дерево перед выходом
                return;
            } else if (userChoice == 2) {
                // Запрашиваем у пользователя правильный ответ и дополнительную информацию
                System.out.println("Какой был правильный ответ?");
                String userAnswer = scanner.nextLine();

                System.out.println("Напишите вопрос, который отличает ваш ответ от текущего.");
                String userQuestion = scanner.nextLine();

                System.out.println("Для вашего ответа какой ответ на ваш вопрос? (да/нет)");
                String userAnswerForQuestion = scanner.nextLine().trim().toLowerCase();

                // Обновляем дерево с новой информацией
                Tree newAnswerNode = new Tree(userAnswer, null, null);
                Tree currentAnswerNode = new Tree(currentTree.getData(), null, null);

                currentTree.setData(userQuestion);
                if (userAnswerForQuestion.equals("да")) {
                    currentTree.setYes(newAnswerNode);
                    currentTree.setNo(currentAnswerNode);
                } else {
                    currentTree.setYes(currentAnswerNode);
                    currentTree.setNo(newAnswerNode);
                }

                saveTree(rootTree); // Сохраняем обновленное дерево
                System.out.println("Сохраняем...");
            } else {
                System.out.println("Некорректный ввод.");
            }
        } else {
            System.out.println("Некорректный ввод.");
        }
    }

    // Метод для создания дефолтного дерева
    private static Tree createDefaultTree() {
        Tree fourthAnswerYes = new Tree("Это кислород?", null, null);
        Tree thirdAnswerYesQuestion = new Tree("Используется ли элемент в дыхательных процессах живых организмов?", fourthAnswerYes, fourthAnswerYes);
        Tree secondAnswerYesQuestion = new Tree("Обладает ли элемент газообразным состоянием при комнатной температуре?", thirdAnswerYesQuestion, thirdAnswerYesQuestion);
        Tree firstAnswerYesQuestion = new Tree("Находится ли элемент в группе неметаллов в периодической таблице?", secondAnswerYesQuestion, secondAnswerYesQuestion);
        Tree mainQuestion = new Tree("Является ли элемент неметеллом?", firstAnswerYesQuestion, firstAnswerYesQuestion);

        mainQuestion.setYes(firstAnswerYesQuestion);
        firstAnswerYesQuestion.setYes(secondAnswerYesQuestion);
        secondAnswerYesQuestion.setYes(thirdAnswerYesQuestion);
        thirdAnswerYesQuestion.setYes(fourthAnswerYes);
        return mainQuestion;
    }

    // Метод для сохранения дерева в файл
    private static void saveTree(Tree rootTree) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("tree.dat"))) {
            oos.writeObject(rootTree);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении дерева: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

// Класс дерева
class Tree implements Serializable {
    private static final long serialVersionUID = 1L;
    private String data;
    private Tree yes;
    private Tree no;

    public Tree(String data, Tree yes, Tree no) {
        this.data = data;
        this.yes = yes;
        this.no = no;
    }

    public boolean isQuestion() {
        return yes != null && no != null;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Tree getYes() {
        return yes;
    }

    public void setYes(Tree yes) {
        this.yes = yes;
    }

    public Tree getNo() {
        return no;
    }

    public void setNo(Tree no) {
        this.no = no;
    }
}
