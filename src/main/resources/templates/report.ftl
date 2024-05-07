<html>
<head>
    <title>CIMET Report Output</title>
    <#include "include/tailwindbase.html">
    <#include "include/tailwindutils.html">
</head>
<body>
<div class="bg-white-800 justify-top relative flex min-h-screen flex-col overflow-hidden py-6 sm:py-12">
    <div class="mx-16">
        <div class="text-base leading-7 text-gray-600 max-w-[70rem]">
            <h1 class="text-2xl font-bold text-emerald-500">CIMET</h1>
            <h3>Change Impact Microservice Evolution Tool</h3>
            <hr class="my-4" />
            <ul class="space-y-1">
                <li class="flex items-center gap-4">
                    <div class="font-semibold text-emerald-600">System Name:</div>
                    <div>${msName}</div>
                </li>
                <li class="flex items-center gap-4">
                    <div class="font-semibold text-emerald-600">Initial Version:</div>
                    <div>${baseVersion}</div>
                </li>
                <li class="flex items-center gap-4">
                    <div class="font-semibold text-emerald-600">Time:</div>
                    <div>${dateTime}</div>
                </li>
            </ul>
            <hr class="my-4" />
            <ul class="space-y-1">
                <li class="flex items-center gap-4">
                    <div class="font-semibold text-emerald-600">Base Branch:</div>
                    <div>${branch1}/${commit1}</div>
                </li>
                <li class="flex items-center gap-4">
                    <div class="font-semibold text-emerald-600">Compare Branch:</div>
                    <div>${branch2}/${commit2}</div>
                </li>
            </ul>
            <hr class="mt-4" />
            <hr class="mb-4 mt-1" />
            <div>
                <h2 class="mb-2 text-xl font-semibold text-sky-600">Overall System Metrics</h2>
                <div class="mb-4 text-sm">
                    <div class="flex flex-row gap-1">
                        <span class="font-semibold text-sky-800 w-48">ADCS Score:</span>
                        <#assign oldScore = systemMetrics.getOldAdcsScore()>
                        <#assign newScore = systemMetrics.getNewAdcsScore()>
                        <#if oldScore &gt; newScore>
                            <div>${oldScore} <span class="text-emerald-500">&rarr;</span> ${newScore}</div>
                        <#elseif oldScore &lt; newScore>
                            <div>${oldScore} <span class="text-rose-500">&rarr;</span> ${newScore}</div>
                        <#else>
                            <div>${newScore}</div>
                        </#if>
                    </div>
                    <div class="text-xs mb-2 text-gray-400">Average number of directly connected services (ADCS): the average of ADS metric of all services. This can be viewed as a measure of coupling, where numbers closer to 0 indicate a low level of coupling.</div>
                    <div class="flex flex-row gap-1">
                        <span class="font-semibold text-sky-800 w-48">SCF Score:</span>
                        <#assign oldScore = systemMetrics.getOldScfScore()>
                        <#assign newScore = systemMetrics.getNewScfScore()>
                        <#if oldScore &gt; newScore>
                            <div>${oldScore} <span class=text-emerald-500">&rarr;</span> ${newScore}</div>
                        <#elseif oldScore &lt; newScore>
                            <div>${oldScore} <span class="text-rose-500">&rarr;</span> ${newScore}</div>
                        <#else>
                            <div>${newScore}</div>
                        </#if>
                    </div>
                    <div class="text-xs mb-2 text-gray-400">Service Coupling Factor (SCF): measure of the density of a graph's connectivity.
                        This value ranges from 0 to 1 where values closer to 0 indicate a less dense graph (less coupled) and numbers closer to 1 indicate a dense (highly coupled) graph
                    </div>
                </div>
                <div class="space-y-2">
                    <div class="pb-2">
                        <div class="font-semibold text-sky-800">Class Metrics:</div>
                    </div>
                    <div class="rounded-lg bg-gray-100 p-4">
                        <div class="grid-cols-4 gap-y-4 gap-x-4 grid">
                            <div class="font-semibold text-slate-900 text-center align-middle p-2 bg-gray-200 bg-opacity-50 rounded-lg">Class Role</div>
                            <div class="font-semibold text-emerald-500 text-center align-middle p-2 bg-gray-200 bg-opacity-50 rounded-lg">Add</div>
                            <div class="font-semibold text-amber-500 text-center align-middle p-2 bg-gray-200 bg-opacity-50 rounded-lg">Modify</div>
                            <div class="font-semibold text-rose-500 text-center align-middle p-2 bg-gray-200 bg-opacity-50 rounded-lg">Delete</div>
                            <#list systemMetrics.getClassMetrics() as classMetric>
                                <#assign isTotal = classMetric.getClassRole().name() == "TOTAL">
                                <#assign font = isTotal?string("text-sky-800 font-semibold not-italic", "")>
                                <div class="text-center align-middle font-semibold uppercase italic text-slate-900 p-2 bg-gray-200 bg-opacity-50 rounded-lg ${font}">${classMetric.getClassRole().name()}</div>
                                <div class="text-center align-middle p-2 bg-gray-200 bg-opacity-50 rounded-lg ${font}">${classMetric.getAddedClassCount()}</div>
                                <div class="text-center align-middle p-2 bg-gray-200 bg-opacity-50 rounded-lg ${font}">${classMetric.getModifiedClassCount()}</div>
                                <div class="text-center align-middle p-2 bg-gray-200 bg-opacity-50 rounded-lg ${font}">${classMetric.getDeletedClassCount()}</div>
                            </#list>
                        </div>
                    </div>
                </div>
            </div>
            <hr class="mt-4" />
            <hr class="mb-4 mt-1" />
            <div>
                <h2 class="mb-4 text-xl font-semibold text-indigo-700">Metrics by Service</h2>
                <div class="rounded-lg bg-pink-50 px-4 py-4 my-2 text-sm">
                    <div>
                        <div><span class="font-semibold text-pink-800">ADS Score</span></div>
                        <p class="text-xs mb-2 text-gray-400">
                            Absolute Dependence of the Service (ADS): The number of services on which the service depends. In other words, ADS is the number of services that S1 calls for its operation to be complete.
                            This can be viewed as a measure of coupling, where numbers closer to 0 indicate a low level of coupling.
                        </p>
                        <#--                        <div><span class="font-semibold text-pink-800">SIUC Score</span></div>-->
                        <#--                        <p class="text-xs mb-2 text-gray-400">-->
                        <#--                            Service Interface Usage Cohesion (SIUC): this metric quantifies the client usage patterns of service operations when a client invokes a service.-->
                        <#--                        </p>-->
                        <div><span class="font-semibold text-pink-800">SIDC Score</span></div>
                        <p class="text-xs mb-2 text-gray-400">
                            Service Interface Data Cohesion (SIDC): this metric quantifies the cohesion of a service based on the cohesiveness of the operations exposed in its interface, which means operations sharing the same type of input parameter.
                            Numbers closer to 1 indicate a higher level of cohesion, while numbers closer to 0 indicate a lower level of cohesion.
                        </p>
                    </div>
                </div>
                <ul class="space-y-4">
                    <#list systemMetrics.getMicroserviceMetrics() as service>
                        <li class="rounded-lg bg-gray-100 p-4">
                            <div class="font-semibold text-indigo-700 mb-2">${service.getId()}</div>
                            <div class="text-sm">
                                <div><span class="font-semibold font-sans text-gray-800">ADS Score:</span>
                                    <#assign oldScore = service.getOldAdsScore()>
                                    <#assign newScore = service.getNewAdsScore()>
                                    <#if oldScore &gt; newScore>
                                        <span>${oldScore} <span class="text-emerald-500">&rarr;</span> ${newScore}</span>
                                    <#elseif oldScore &lt; newScore>
                                        <span>${oldScore} <span class="text-rose-500">&rarr;</span> ${newScore}</span>
                                    <#else>
                                        <span>${newScore}</span>
                                    </#if>
                                </div>
                                <#--                                <div><span class="font-semibold font-sans text-gray-800">SIUC Score:</span> ${service.getNewSiucScore()}</div>-->
                                <div><span class="font-semibold font-sans text-gray-800">SIDC Score:</span>
                                    <#assign oldScore = service.getOldSidc2Score()>
                                    <#assign newScore = service.getOldSidc2Score()>
                                    <#if oldScore &lt; newScore>
                                        <span>${oldScore} <span class="text-emerald-500">&rarr;</span> ${newScore}</span>
                                    <#elseif oldScore &gt; newScore>
                                        <span>${oldScore} <span class="text-rose-500">&rarr;</span> ${newScore}</span>
                                    <#else>
                                        <span>${newScore}</span>
                                    </#if>
                                </div>
                            </div>
                            <#assign numCallChanges = service.getDependencyMetrics().getCallChanges()?size>
                            <#if numCallChanges &gt; 0>
                                <div class="mt-2 mb-1 font-semibold text-indigo-700">Call Changes:</div>
                                <ul class="text-sm space-y-2">
                                    <#list service.getDependencyMetrics().getCallChanges() as change>
                                        <#switch change.getChangeType().name()>
                                            <#case "ADD">
                                                <#assign color = "text-emerald-700 font-semibold">
                                                <#break>
                                            <#case "MODIFY">
                                                <#assign color = "text-amber-700 font-semibold">
                                                <#break>
                                            <#case "DELETE">
                                                <#assign color = "text-rose-700 font-semibold">
                                                <#break>
                                            <#default>
                                                <#assign color = "">
                                        </#switch>
                                        <li class="flex flex-col gap-1 rounded-lg bg-gray-200 p-4">
                                            <#assign old = change.getOldCall()>
                                            <#assign new = change.getNewCall()>
                                            <div class="${color} mb-2 font-bold text-base">
                                                ${change.getChangeType().name()}
                                            </div>
                                            <div class="flex flex-row gap-1">
                                                <div class="font-semibold font-sans text-slate-900 w-36 flex-shrink-0">Dest. Service:</div>
                                                <div class="">
                                                    <#assign oldItem = old.getDestMsId()>
                                                    <#assign newItem = new.getDestMsId()>
                                                    <#if oldItem != newItem>
                                                        <span class="${color}">${oldItem} &rarr; ${newItem}</span>
                                                    <#else>
                                                        ${newItem}
                                                    </#if>
                                                </div>
                                            </div>
                                            <div class="flex flex-row gap-1">
                                                <div class="font-semibold font-sans text-slate-900 w-36 flex-shrink-0">Dest. Api:</div>
                                                <div class="">
                                                    <#assign oldItem = old.getDestEndpoint()>
                                                    <#assign newItem = new.getDestEndpoint()>
                                                    <#if oldItem != newItem>
                                                        <span class="${color}">${oldItem} &rarr; ${newItem}</span>
                                                    <#else>
                                                        ${newItem}
                                                    </#if>
                                                </div>
                                            </div>
                                            <div class="flex flex-row gap-1">
                                                <div class="font-semibold font-sans text-slate-900 w-36 flex-shrink-0">Http Method:</div>
                                                <div class="">
                                                    <#assign oldItem = old.getHttpMethod()>
                                                    <#assign newItem = new.getHttpMethod()>
                                                    <#if oldItem != newItem>
                                                        <span class="${color}">${oldItem} &rarr; ${newItem}</span>
                                                    <#else>
                                                        ${newItem}
                                                    </#if>
                                                </div>
                                            </div>
                                            <div class="flex flex-row gap-1">
                                                <div class="font-semibold font-sans text-slate-900 w-36 flex-shrink-0">In Method:</div>
                                                <div class="">
                                                    <#assign oldItem = old.getCalledFrom()>
                                                    <#assign newItem = new.getCalledFrom()>
                                                    <#if oldItem != newItem>
                                                        <span class="${color}">${oldItem} &rarr; ${newItem}</span>
                                                    <#else>
                                                        ${newItem}
                                                    </#if>
                                                </div>
                                            </div>
                                            <#assign callImpact = change.getImpact()>
                                            <div class="flex flex-row gap-1">
                                                <#if callImpact.getImpact().name() != "NONE">
                                                    <div class="font-semibold text-indigo-700 w-36 flex-shrink-0">Effect:</div>
                                                    <div class="">
                                                        <span class="font-semibold">${callImpact.getName()}: </span>
                                                        ${callImpact.getMessage()}
                                                    </div>
                                                </#if>
                                            </div>
                                            <div class="flex flex-row gap-1">
                                                <#if callImpact.getImpact().name() != "NONE">
                                                    <div class="font-semibold text-indigo-700 w-36 flex-shrink-0">Impact:</div>
                                                    <div class="">
                                                        <span class="font-semibold">${callImpact.getImpact().getName()}: </span>
                                                        ${callImpact.getImpactMsg()}
                                                    </div>
                                                </#if>
                                            </div>
                                        </li>
                                    </#list>
                                </ul>
                            </#if>
                            <#assign numEndpointChanges = service.getDependencyMetrics().getEndpointChanges()?size>
                            <#if numEndpointChanges &gt; 0>
                                <div class="mt-2 mb-1 font-semibold text-indigo-700">Endpoint Changes:</div>
                                <ul class="text-sm space-y-2">
                                    <#list service.getDependencyMetrics().getEndpointChanges() as change>
                                        <#switch change.getChangeType().name()>
                                            <#case "ADD">
                                                <#assign color = "text-emerald-700 font-semibold">
                                                <#break>
                                            <#case "MODIFY">
                                                <#assign color = "text-amber-700 font-semibold">
                                                <#break>
                                            <#case "DELETE">
                                                <#assign color = "text-rose-700 font-semibold">
                                                <#break>
                                            <#default>
                                                <#assign color = "">
                                        </#switch>
                                        <li class="flex flex-col gap-1 rounded-lg bg-gray-200 p-4">
                                            <#assign old = change.getOldEndpoint()>
                                            <#assign new = change.getNewEndpoint()>
                                            <div class="${color} mb-2 col-span-2 font-bold text-base">
                                                ${change.getChangeType().name()}
                                            </div>
                                            <div class="flex flex-row gap-1">
                                                <div class="font-semibold font-sans text-slate-900 w-36 flex-shrink-0">Endpoint:</div>
                                                <div class="font-mono ">
                                                    <#if old.getUrl() != new.getUrl()>
                                                        <span class="${color}">${old.getUrl()} &rarr; ${new.getUrl()}</span>
                                                    <#else>
                                                        ${new.getUrl()}
                                                    </#if>
                                                </div>
                                            </div>
                                            <div class="flex flex-row gap-1">
                                                <div class="font-semibold font-sans text-slate-900 w-36 flex-shrink-0">HttpMethod:</div>
                                                <div class="font-mono ">
                                                    <#if old.getHttpMethod() != new.getHttpMethod()>
                                                        <span class="${color}">${old.getHttpMethod()} &rarr; ${new.getHttpMethod()}</span>
                                                    <#else>
                                                        ${new.getHttpMethod()}
                                                    </#if>
                                                </div>
                                            </div>
                                            <div class="flex flex-row gap-1">
                                                <div class="font-semibold font-sans text-slate-900 w-36 flex-shrink-0">Parameters:</div>
                                                <div class="font-mono ">
                                                    <#if old.getParameterList() != new.getParameterList()>
                                                        <span class="${color}">${old.getParameterList()} &rarr; ${new.getParameterList()}</span>
                                                    <#else>
                                                        ${new.getParameterList()}
                                                    </#if>
                                                </div>
                                            </div>
                                            <div class="flex flex-row gap-1">
                                                <div class="font-semibold font-sans text-slate-900 w-36 flex-shrink-0">Return:</div>
                                                <div class="font-mono ">
                                                    <#if old.getReturnType() != new.getReturnType()>
                                                        <span class="${color}">${old.getReturnType()} &rarr; ${new.getReturnType()}</span>
                                                    <#else>
                                                        ${new.getReturnType()}
                                                    </#if>
                                                </div>
                                            </div>
                                            <div class="flex flex-row gap-1 mb-2">
                                                <div class="font-semibold font-sans text-slate-900 w-36 flex-shrink-0">Method:</div>
                                                <div class="">
                                                    <#if old.getMethodName() != new.getMethodName()>
                                                        <span class="${color}">${old.getMethodName()} &rarr; ${new.getMethodName()}</span>
                                                    <#else>
                                                        ${new.getMethodName()}
                                                    </#if>
                                                </div>
                                            </div>
                                            <#assign endpointImpact = change.getImpact()>
                                            <div class="flex flex-row gap-1">
                                                <div class="font-semibold text-indigo-700 w-36 flex-shrink-0"><#if endpointImpact.getImpact().name() != "NONE">Effect:</#if></div>
                                                <div class=""><#if endpointImpact.getImpact().name() != "NONE">
                                                        <span class="font-semibold">${endpointImpact.getName()}: </span>
                                                        ${endpointImpact.getMessage()}
                                                    </#if>
                                                </div>
                                            </div>
                                            <div class="flex flex-row gap-1">
                                                <div class="font-semibold text-indigo-700 w-36 flex-shrink-0"><#if endpointImpact.getImpact().name() != "NONE">Impact:</#if></div>
                                                <div class="">
                                                    <#if endpointImpact.getImpact().name() != "NONE">
                                                        <span class="font-semibold">${endpointImpact.getImpact().getName()}: </span>
                                                        ${endpointImpact.getImpactMsg()}
                                                    </#if>
                                                </div>
                                            </div>
                                        </li>
                                    </#list>
                                </ul>
                            </#if>
                        </li>
                    </#list>
                </ul>
            </div>
        </div>
    </div>
</div>

</body>
</html>