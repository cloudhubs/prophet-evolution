<html>
<head>
    <title>CIMET Report Output</title>
    <#include "include/tailwindbase.html">
    <#include "include/tailwindutils.html">
</head>
<body>
<div class="bg-white-800 justify-top relative flex min-h-screen flex-col overflow-hidden py-6 sm:py-12">
    <div class="mx-16">
        <div class="text-base leading-7 text-gray-600">
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
                <h2 class="mb-4 text-xl font-semibold text-sky-600">Overall System Metrics</h2>
                <div class="space-y-2">
                    <div class="rounded-lg bg-gray-100 px-4 py-2">
                        <div class="pb-2">
                            <div class="font-semibold text-sky-500">Class Metrics:</div>
                        </div>
                        <ul class="space-y-2 pb-2">
                            <#list systemMetrics.getClassMetrics() as classMetric>
                            <li class="gap-4 rounded-lg bg-gray-200 px-4 py-2">
                                <div class="pb-2 font-semibold uppercase italic text-slate-900">${classMetric.getClassRole().name()}</div>
                                <ul>
                                <#list classMetric.getMetricsAsMap() as key, value>
                                    <li class="flex items-center gap-4">
                                        <div class="font-semibold text-sky-800">${key}:</div>
                                        <div>${value}</div>
                                    </li>
                                </#list>
                                </ul>
                            </li>
                            </#list>
                        </ul>
                    </div>
<#--                    <div class="rounded-lg bg-gray-100 px-4 py-2">-->
<#--                        <div class="pb-2">-->
<#--                            <div class="font-semibold text-sky-500">Dependency Metrics:</div>-->
<#--                        </div>-->
<#--                        <ul class="space-y-2 pb-2">-->
<#--                            <li class="gap-4 rounded-lg bg-gray-200 px-4 py-2">-->
<#--                                <div class="pb-2 font-semibold uppercase italic text-slate-900">${dependencyMetric.getClassRole().getName()}</div>-->
<#--                                <ul>-->
<#--                                    <li class="flex items-center gap-4">-->
<#--                                        <div class="font-semibold text-sky-800">${dependencyMetric.key()}:</div>-->
<#--                                        <div>${dependencyMetric.value()}</div>-->
<#--                                    </li>-->
<#--                                </ul>-->
<#--                            </li>-->
<#--                        </ul>-->
<#--                    </div>-->
                </div>
            </div>
            <hr class="mt-4" />
            <hr class="mb-4 mt-1" />
            <div>
                <h2 class="mb-4 text-xl font-semibold text-violet-800">Metrics by Service</h2>
                <ul class="space-y-1">
                    <li class="flex items-center gap-4 rounded-lg bg-gray-100 px-4 py-2"></li>
                </ul>
            </div>
        </div>
    </div>
</div>

</body>
</html>