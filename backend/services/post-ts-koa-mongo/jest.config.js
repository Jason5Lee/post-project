module.exports = {
    moduleFileExtensions: [
        "js",
        "ts",
    ],
    transform: {
        "^.+\\.(ts|tsx)$": [
            "ts-jest",
            {
                tsconfig: "tsconfig.json",
            },
        ],
    },
    testMatch: [
        "**/test/**/*.test.(ts|js)",
    ],
    testEnvironment: "node",
    preset: "ts-jest",
};
