# Итоговый проект по Java "Сервис коротких ссылок"
## Описание
Консольное приложение коротких ссылок. Функционал:
- Создание коротких ссылок;
- Переход по коротким ссылкам.

## Сборка приложения
Для сборки необходимо выполнить:
mvn -U clean install -Dfile.encoding=UTF-8

## Параметры приложения
link.days.max - максимальное время жизни ссылки (в днях), задается системно из файла
с параметрами app.properties 

## Запуск приложения
Для запуска и работы приложения требуется JRE 17 и интернет-браузер. Команда запуска:
java -jar shorten_link-1.0.jar