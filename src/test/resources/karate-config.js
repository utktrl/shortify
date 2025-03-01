function fn() {
    var env = karate.env || 'local'; // get java system property 'karate.env'
    karate.log('karate.env system property was:', env);

    var config = { // base config JSON
        baseURL: 'https://localhost:8080',
    };
    if (env == 'stage') {
        // over-ride only those that need to be
        config.baseURL = 'https://stage-host/v1/auth';
    } else if (env == 'e2e') {
        config.baseURL = 'https://e2e-host/v1/auth';
    }
    karate.configure('connectTimeout', 5000);
    karate.configure('readTimeout', 5000);
    return config;
}