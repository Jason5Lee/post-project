import bcrypt from "bcrypt";
import { Password } from "..";

export class Encryption {
    constructor(public encryptionCost: number) {}

    encrypt(password: string): Promise<string> {
        return new Promise((resolve, reject) => {
            bcrypt.hash(password, this.encryptionCost, (err, hash) => {
                if (err) {
                    reject(err);
                } else {
                    resolve(hash);
                }
            });
        });
    }

    getValidator(hash: string): (password: Password) => Promise<boolean> {
        return password => {
            return new Promise((resolve, reject) => {
                bcrypt.compare(password.plain, hash, (err, res) => {
                    if (err) {
                        reject(err);
                    } else {
                        resolve(res);
                    }
                });
            });
        };
    }
}
