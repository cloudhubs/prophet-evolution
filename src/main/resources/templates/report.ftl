<html>
<head>
    <title>CIMET Report Output</title>
    <#include "include/tailwindbase.html">
    <#include "include/tailwindutils.html">
</head>
<div class="bg-white-800 justify-top relative flex min-h-screen flex-col overflow-hidden py-6 sm:py-12">
    <div class="mx-16">
        <div class="text-base leading-7 text-gray-600">
            <div class="text-2xl font-bold text-emerald-500">CIMET</div>
            <div>Change Impact Microservice Evolution Tool</div>
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
            <hr class="mb-8 mt-1" />
            <#list services>
            <ul>
                <#items as msModel, deltaList>
                    <ul class="space-y-1">
                        <li class="flex items-center gap-4">
                            <div class="font-semibold text-sky-600">${msModel.getId()}</div>
                        </li>
                        <li class="flex items-center gap-4">
                            <div class="text-sky-600">Base Commit:</div>
                            <div>${msModel.getCommit()}</div>
                        </li>
                        <li class="flex items-center gap-4">
                            <div class="font-semibold text-purple-600">Changes:</div>
                        </li>
                        <#list deltaList>
                            <ul class="space-y-1">
                                <#items as delta>
                                    <div class="rounded-lg bg-gray-100 px-4 py-4">
                                        <li class="flex items-center gap-4">
                                            <div class="text-purple-600">Commit:</div>
                                            <div>${delta.getCommitId()}</div>
                                        </li>
                                        <li class="flex items-center gap-4">
                                            <div class="text-purple-600">Change Type:</div>
                                            <div>${delta.getChangeType()}</div>
                                        </li>
                                    </div>
                                </#items>
                            </ul>
                        </#list>
                    </ul>
                    <hr class="my-4" />
                </#items>
            </ul>
            <#else>
                <p>No services found</p>
            </#list>
        </div>
    </div>
</div>
</html>