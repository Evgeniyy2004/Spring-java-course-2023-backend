<#if update>
    <b>Изменения в расписании!</b>

</#if>
<b>Расписание на ${day}</b>
<#if someCondition>
    <b>Занятий нет!</b>

<#else>
        <#list subjects as subject>
            <#assign index = subject_index>
⏰${time[index]}
${subject}
${classrooms[index]}
            <#if index < (subjects?size - 1)>

            </#if>
</#list>
</#if>

