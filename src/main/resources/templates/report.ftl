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
                    <div>${branch1}/{$commit1}</div>
                </li>
                <li class="flex items-center gap-4">
                    <div class="font-semibold text-emerald-600">Compare Branch:</div>
                    <div>${branch2}/{$commit2}</div>
                </li>
            </ul>
            <hr class="mt-4" />
            <hr class="mb-4 mt-1" />
            <div>
                <h2 class="mb-2 text-xl font-semibold text-sky-600">Overall System Metrics</h2>
                <div class="mb-4 text-sm">
                    <div><span class="font-semibold text-sky-800">ADCS Score:</span> {$systemMetrics.getAdcsScore()}</div>
                    <div class="text-xs mb-2 text-gray-400">Average number of directly connected services (ADCS): the average of ADS metric of all services.</div>
                    <div><span class="font-semibold text-sky-800">SCF Score:</span> {$systemMetrics.getScfScore()}</div>
                    <div class="text-xs mb-2 text-gray-400">Service Coupling Factor (SCF): measure of the density of a graph's connectivity. SCF = SC/(N2 - N)</div>
                </div>
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
                </div>
            </div>
            <hr class="mt-4" />
            <hr class="mb-4 mt-1" />
            <div>
                <h2 class="mb-4 text-xl font-semibold text-violet-800">Metrics by Service</h2>
                <div class="rounded-lg bg-pink-50 px-4 py-4 my-2 text-sm">
                    <div>
                        <div><span class="font-semibold text-pink-800">ADS Score</span></div>
                        <p class="text-xs mb-2 text-gray-400">
                            Absolute Dependence of the Service (ADS): number of services on which the service depends. In other words, ADS is the number of services that S1 calls for its operation to be complete.
                        </p>
                        <div><span class="font-semibold text-pink-800">SIUC Score</span></div>
                        <p class="text-xs mb-2 text-gray-400">
                            Service Interface Usage Cohesion (SIUC): this metric quantifies the client usage patterns of service operations when a client invokes a service.
                        </p>
                        <div><span class="font-semibold text-pink-800">SIDC Score</span></div>
                        <p class="text-xs mb-2 text-gray-400">
                            Service Interface Data Cohesion (SIDC): this metric quantifies the cohesion of a service based on the cohesiveness of the operations exposed in its interface, which means operations sharing the same type of input parameter.
                        </p>
                    </div>
                </div>
                <ul class="space-y-1">
                    <#list systemMetrics.getMicroserviceMetrics() as service>
                        <li class="rounded-lg bg-gray-100 px-4 py-2">
                            <div class="font-semibold text-violet-900 mb-2">${service.name()}</div>
                            <div class="text-sm">
                                <div><span class="font-semibold font-sans text-gray-800">ADS Score:</span> {$service.getAdsScore()}</div>
                                <div><span class="font-semibold font-sans text-gray-800">SIUC Score:</span> {$service.getSiucScore()}</div>
                                <div><span class="font-semibold font-sans text-gray-800">SIDC Score:</span> {$service.getSidc2Score()}</div>
                            </div>
                            <div class="mt-2 mb-1 font-semibold text-teal-600">Call Changes:</div>
                            <ul class="text-sm">
                                <#list service.getDependencyMetrics().getCallChanges() as change>
                                    <li class="gap-4 rounded-lg bg-gray-200 px-4 py-2">
                                        <div class="mb-4">
                                            <div><span class="font-semibold font-sans text-teal-800">Dest. Service:</span> {$change.getNewLink().getMsDestination()}</div>
                                            <div><span class="font-semibold font-sans text-teal-800">Dest. Route:</span> {$change.getNewCall().getApi()}</div>
                                            <div class="font-mono uppercase"><span class="font-semibold font-sans text-teal-800 normal-case">HttpMethod:</span> {$change.getNewCall().getHttpMethod()}</div>
                                        </div>
                                        <div ><span class="font-semibold font-sans text-violet-800">Method Name:</span> {$change.getNewCall().getMethodName()}</div>
                                        <div class="mb-2 italic text-gray-500"><span class="font-semibold font-sans not-italic text-violet-800">FilePath:</span> {$change.getNewCall().getSourceFile()}</div>

                                        <div><span class="font-semibold text-red-800">Change:</span> {$change.getChangeType().getName()}</div>
                                        <div><span class="font-semibold text-red-800">Impact:</span> {$change.getImpact().getName()}</div>
                                        <div><span class="font-semibold text-red-800">Risk:</span> [TODO]</div>
                                    </li>
                                </#list>
                            </ul>
                            <div class="mt-2 mb-1 font-semibold text-teal-600">Endpoint Changes:</div>
                            <ul class="text-sm">
                                <#list service.getDependencyMetrics().getEndpointChanges() as change>
                                    <li class="gap-4 rounded-lg bg-gray-200 px-4 py-2">
                                        <div class="font-mono"><span class="font-semibold font-sans text-teal-800">Endpoint:</span> {$change.getNewEndpoint().getUrl()}</div>
                                        <div class="font-mono"><span class="font-semibold font-sans text-teal-800">HttpMethod:</span> {$change.getNewEndpoint().getHttpMethod()}</div>
                                        <div class="font-mono"><span class="font-semibold font-sans text-teal-800">Parameters:</span> {$change.getNewEndpoint().getParameterList()}</div>
                                        <div class="font-mono mb-2"><span class="font-semibold font-sans text-teal-800">Return:</span> {$change.getNewEndpoint().getReturnType()}</div>
                                        <div class="mb-2"><span class="font-semibold font-sans text-violet-800">Method Name:</span> {$change.getNewEndpoint().getMethodName()}</div>
                                        <div><span class="font-semibold text-red-800">Change:</span> {$change.getChangeType().name()}</div>
                                        <div><span class="font-semibold text-red-800">Impact:</span> {$change.getImpact().getName()}</div>
                                        <div><span class="font-semibold text-red-800">Risk:</span> [TODO]</div>
                                    </li>
                                </#list>
                            </ul>
                            <div class="mt-2 mb-1 font-semibold text-blue-700">Entity Changes:</div>
                            <ul class="text-sm">
                                <#list service.getEntityChangeList() as change>
                                    <li class="gap-4 rounded-lg bg-gray-200 px-4 py-2">

                                    </li>
                                </#list>
                            </ul>
                        </li>
                    </#list>
                </ul>
            </div>
        </div>
    </div>
</div>

</body>
</html>