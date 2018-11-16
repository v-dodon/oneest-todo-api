package com.oneest.task.todo.dao

import com.oneest.task.todo.model.Tag
import com.oneest.task.todo.model.Todo
import org.apache.commons.lang3.StringUtils
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

class TodoSpecification(var filter: Todo) : Specification<Todo> {

    override fun toPredicate(root: Root<Todo>, cq: CriteriaQuery<*>, cb: CriteriaBuilder): Predicate {
        val predicate = cb.disjunction()
        if (StringUtils.isNotEmpty(filter.title)){
            predicate.expressions.add(cb.like(root.get("title"), "%" + filter.title + "%"))
        }
        if(StringUtils.isNotEmpty(filter.text)) {
            predicate.expressions.add(cb.like(root.get("text"), "%" + filter.text + "%"))
        }

        if(filter.tag != null) {
            predicate.expressions.add(cb.equal(root.get<Tag>("tag"), filter.tag))
        }

        return predicate
    }
}