/*
 * Copyright (c) 2018 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package motif.ir.source

import motif.ir.source.accessmethod.AccessMethod
import motif.ir.source.base.Dependency
import motif.ir.source.base.Type
import motif.ir.source.child.ChildMethod
import motif.ir.source.dependencies.AnnotatedDependency
import motif.ir.source.dependencies.Dependencies
import motif.ir.source.dependencies.ExplicitDependencies
import motif.ir.source.objects.FactoryMethod
import motif.ir.source.objects.ObjectsClass

class ScopeClass(
        val userData: Any?,
        val type: Type,
        val childMethods: List<ChildMethod>,
        val accessMethods: List<AccessMethod>,
        val objectsClass: ObjectsClass?,
        val explicitDependencies: ExplicitDependencies?) {

    val scopeDependency = Dependency(userData, type, null)

    val factoryMethods: List<FactoryMethod> = objectsClass?.factoryMethods ?: listOf()

    private val allProvided: List<Dependency> = factoryMethods.flatMap { it.providedDependencies } + scopeDependency

    private val consumed: List<Dependency> = factoryMethods.flatMap { it.consumedDependencies } + accessMethods.map { it.dependency }

    val exposed: List<Dependency> = factoryMethods.filter { it.isExposed }.flatMap { it.providedDependencies }

    val selfDependencies: Dependencies by lazy {
        val annotatedDependencies = (consumed - allProvided).map { AnnotatedDependency(it, false, setOf(type)) }
        Dependencies(annotatedDependencies)
    }

    override fun toString(): String {
        return type.toString()
    }
}