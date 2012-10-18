<%@ include file="/common/taglibs.jsp" %>
<%@ page pageEncoding="UTF-8" %>

<a name="top"></a><span class="pageHeader">Повседневные действия</span><br/>
<ul>
    <li><a href="#create">Создание задания</a></li>
    <li><a href="#edit">Редактирование задания</a></li>
    <li><a href="#list">Просмотр заданий</a></li>
    <li><a href="#search">Поиск заданий</a></li>
    <li><a href="#report">Генерация отчётов</a></li>
    <li><a href="#prefs">Изменение настроек</a></li>
</ul>

<hr width="75%" noshade height="1"/>
<a name="create"></a><span class="editColumnTitle">Создание задания</span>
<a href="#top" class="headerLinks">[наверх]</a><br/>

<p class="help">
    Чтобы создать новое задание, вначале перейдите на страницу Список проектов, щёлкнув на
    одноименной ссылке в головном меню. Далее щёлкните на иконке Создать новое задание
    (<html:img styleClass="helpImage" src="../themes/defaulttheme/images/create.gif"/>)
    рядом с проектом, для которого вы хотите его создать.<br/>
    <br/>
    Вам необходимо иметь полномочие Создать в проекте перед тем, как вы сможете создавать для
    него новое задание.<br/>
</p>

<hr width="75%" noshade height="1"/>
<a name="edit"></a><span class="editColumnTitle">Редактирование задания</span>
<a href="#top" class="headerLinks">[наверх]</a><br/>

<p class="help">
    Есть несколько способов отредактировать задание.<br/>
    <br/>
    Если задание отображается на странице Мой ITracker, вы можете щёлкнуть иконку Редактировать
    (<html:img styleClass="helpImage" src="../themes/defaulttheme/images/edit.gif"/>) рядом
    с этим заданием.<br/>
    <br/>
    Когда вы просматриваете детали задания, иконка Редактировать
    (<html:img styleClass="helpImage" src="../themes/defaulttheme/images/edit.gif"/>) будет
    отображаться в области действий.<br/>
    <br/>
    Чтобы отредактировать существующее задание, сначала перейдите на страницу Список проектов,
    щёлкнув на одноименной ссылке в головном меню. На данной странице щёлкните иконку Просмотр
    заданий в проекте (<html:img styleClass="helpImage" src="../themes/defaulttheme/images/view.gif"/>)
    рядом с интересующим проектом. Имея список заданий, воспользуйтесь иконкой Редактировать
    (<html:img styleClass="helpImage" src="../themes/defaulttheme/images/edit.gif"/>)
    радом с нужным заданием.<br/>
    <br/>
    Вам необходимо иметь полномочие Редактировать в проекте перед тем, как вы сможете
    редактировать в нём существующие задания.<br/>
</p>

<hr width="75%" noshade height="1"/>
<a name="list"></a><span class="editColumnTitle">Просмотр заданий</span>
<a href="#top" class="headerLinks">[наверх]</a><br/>

<p class="help">
    Чтобы посмотреть список заданий для проекта, перейдите на страницу Список проектов,
    щёлкнув на одноименной ссылке в головном меню. Далее нажмите иконку Просмотр заданий в
    проекте (<html:img styleClass="helpImage" src="../themes/defaulttheme/images/view.gif"/>)
    рядом с проектом, задания которого хотите посмотреть. На данной странице вы можете увидеть
    детали задания, отслеживать его, если вы уже не получали уведомлений об изменениях, или
    редактировать задание, при наличии соответствующих привилегий.<br/>
</p>


<hr width="75%" noshade height="1"/>
<a name="search"></a><span class="editColumnTitle">Поиск заданий</span>
<a href="#top" class="headerLinks">[наверх]</a><br/>

<p class="help">
    У вас есть возможность искать задания в проектах по их серьёзности и коде статуса. Со
    страницы поиска вы можете выбирать несколько значений одновременно, удерживая клавишу Ctrl.
    Возможно также выбрать диапазон значений, щёлкнув на первом элементе и, удерживая Shift,
    на последнем элементе. Указав критерии поиска и сортировки, щёлкните Поиск, чтобы найти
    все совпадения.
    <br/>
    Можно также выполнить более детальный поиск в конкретном проекте, щёлкнув на иконке Поиск
    задания (<html:img styleClass="helpImage" src="../themes/defaulttheme/images/search.gif"/>)
    рядом с проектом на странице Список проектов.
    <br/>
    Из секции результатов вы можете редактировать или просматривать задания при наличии
    соответствующих полномочий, щёлкнув на соответствующей иконке.
    <br/>
</p>

<hr width="75%" noshade height="1"/>
<a name="report"></a><span class="editColumnTitle">Генерация отчётов</span>
<a href="#top" class="headerLinks">[наверх]</a><br/>

<p class="help">
    Для генерации отчёта, сначала отметьте позиции рядом с проектами, которые желаете
    включить в отчёт. Затем выберите требуемый отчёт из выпадающего меню и нажмите кнопку
    генерации отчёта. Отчёт будет сгенерирован, а результаты отображены в вашем текущем окне
    браузера. В зависимости от числа заданий в проекте и количества выбранных проектов, может
    потребоваться до нескольких минут для обработки отчёта.
    <br/>
</p>

<hr width="75%" noshade height="1"/>
<a name="prefs"></a><span class="editColumnTitle">Изменение настроек</span>
<a href="#top" class="headerLinks">[наверх]</a><br/>

<p class="help">
    Чтоб изменить настройки щёлкните на ссылке Мои настройки в головном меню. Это приведёт вас
    на страницу, позволяющую изменить вашу личную информацию. Некоторые настройки изменяют
    внешний вид сайта.<br/>
    <br/>
    При включении опции автоматического входа в систему, на вашем компьютере будет сохранён
    постоянный куки-файл с вашим идентификатором пользователя и паролем. Это может вызвать
    проблемы безопасности, так что убедитесь, что вам нужна данная опция прежде чем включать её.
    <br/>
    <br/>
    При указании количества отображаемых заданий на странице, вы можете ввести 0 или
    отрицательное значение чтобы отобразить все. Любое положительное число ограничит
    количество отображаемых элементов.
    <br/>
</p>