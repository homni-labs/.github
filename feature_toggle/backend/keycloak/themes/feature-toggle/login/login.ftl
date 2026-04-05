<#-- Custom Feature Toggle login theme -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Feature Toggle — Sign In</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${url.resourcesPath}/css/styles.css">
</head>
<body>

<!-- Animated background toggles (CSS-only) -->
<div class="bg">
    <div class="bg-gradient"></div>
    <div class="mover m1"><div class="toggle t1"><span></span></div></div>
    <div class="mover m2"><div class="toggle t2"><span></span></div></div>
    <div class="mover m3"><div class="toggle t3"><span></span></div></div>
    <div class="mover m4"><div class="toggle t4"><span></span></div></div>
    <div class="mover m5"><div class="toggle t5"><span></span></div></div>
    <div class="mover m6"><div class="toggle t6"><span></span></div></div>
    <div class="mover m7"><div class="toggle t7"><span></span></div></div>
    <div class="mover m8"><div class="toggle t8"><span></span></div></div>
    <div class="mover m9"><div class="toggle t9"><span></span></div></div>
    <div class="mover m10"><div class="toggle t10"><span></span></div></div>
    <div class="vignette"></div>
</div>

<main class="main">
    <!-- Wide: row layout  |  Narrow: column layout -->
    <div class="layout">

        <!-- Branding -->
        <div class="branding">
            <div class="logo-wrap">
                <div class="logo-circle">
                    <img src="${url.resourcesPath}/img/logo.jpeg" alt="Feature Toggle" class="logo-img">
                </div>
            </div>
            <h1 class="brand-title">Feature Toggle</h1>
            <p class="brand-subtitle">Control your features with confidence</p>
        </div>

        <!-- Login card -->
        <div class="card">
            <h2 class="card-title">Welcome back</h2>
            <p class="card-subtitle">Sign in to your account</p>

            <#if message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
                <div class="alert alert-${message.type}">
                    ${kcSanitize(message.summary)?no_esc}
                </div>
            </#if>

            <form action="${url.loginAction}" method="post" class="form">
                <!-- Username / Email -->
                <div class="field">
                    <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                        <rect x="2" y="4" width="20" height="16" rx="3"/>
                        <path d="M2 7l10 7 10-7"/>
                    </svg>
                    <input type="text"
                           id="username"
                           name="username"
                           value="${(login.username!'')}"
                           placeholder="Email or username"
                           autocomplete="username"
                           autofocus>
                </div>

                <!-- Password -->
                <div class="field">
                    <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                        <rect x="3" y="11" width="18" height="11" rx="2"/>
                        <path d="M7 11V7a5 5 0 0110 0v4"/>
                    </svg>
                    <input type="password"
                           id="password"
                           name="password"
                           placeholder="Password"
                           autocomplete="current-password">
                </div>

                <!-- Submit -->
                <button type="submit" class="btn-login">
                    <span class="btn-shimmer"></span>
                    <span class="btn-text">Sign In</span>
                </button>
            </form>

            <#if realm.password && social?? && social.providers?has_content>
                <div class="divider">
                    <span>or continue with</span>
                </div>
                <div class="social-providers">
                    <#list social.providers as p>
                        <a href="${p.loginUrl}" class="social-btn" title="${p.displayName}">
                            ${p.displayName}
                        </a>
                    </#list>
                </div>
            </#if>
        </div>
    </div>
</main>

</body>
</html>
